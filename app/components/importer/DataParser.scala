package components.importer

import java.util.Currency
import org.joda.time.{DateTime, LocalDate}
import models.db.{TransactionTypes, Purses}
import models.{Transaction, PurseBalance, TransactionType, Purse}
import scala.collection.mutable

class DataParser {

  val DefaultPurse = "к"
  val orderingHolder = new OrderingHolder

  def reset {
    orderingHolder.reset
  }

	def parseRow(row: String): ParsedRow = {

    try {
      (row + ";;;;;;;; ").split(";").map(_.trim).slice(0, 8).toList match {	// TODO: better solution for adding to specific length
        case
          List(
            date: String,
            amountCurrencyString: String,
            typeName: String,
            description: String,
            srcPurse: String,
            dstPurse: String,
            srcPurseBalance: String,
            dstPurseBalance: String
          ) =>
            compileRow(
              TypeParser.parseRequired(typeName),	// TODO: fix IDE bug
              DateParser.parseRequired(date),
              PurseParser.parseRequired(if (!srcPurse.isEmpty) srcPurse else DefaultPurse),
              PurseParser.parse(dstPurse),
              AmountCurrencyParser.parseRequired(amountCurrencyString),
              StringParser.parse(description),
              AmountCurrencyParser.parse(srcPurseBalance),
              AmountCurrencyParser.parse(dstPurseBalance)
            )
        case other => throw new ParseException(s"Can not parse row '${row}', it is parsed as: " + other.toString)
      }
    } catch {
      case e: Throwable =>
        throw new ParseException(s"Error parsing row '${row}': " + e.getMessage)
    }
	}

	private def compileRow(
		transactionType: TransactionType,
		date: LocalDate,
		srcPurse: Purse,
		dstPurse: Option[Purse],
		amountCurrency: AmountCurrency,
		description: Option[String],
		srcPurseBalanceAmount: Option[AmountCurrency],
		dstPurseBalanceAmount: Option[AmountCurrency]
	): ParsedRow = {

		val ordering = orderingHolder.getNext(date.toString)

		ParsedRow(
			Some(	// TODO: consider case without transactions
				Transaction(
					None,
					transactionType.id.get,
					date,
					ordering,
					DateTime.now(),
					srcPurse.id.get,
					dstPurse.map(_.id.get),
					-amountCurrency.amount,
					amountCurrency.currency,
					description.getOrElse("")
				)
			),
			if (srcPurseBalanceAmount.isDefined)	// TODO: refactor
				Some(
					PurseBalance(
						None,
						srcPurse.id.get,
						date,
						ordering,
						srcPurseBalanceAmount.get.amount,
						srcPurseBalanceAmount.get.currency,
            true
					)
				)
			else
				None,
			if (dstPurseBalanceAmount.isDefined && dstPurse.isDefined)	// TODO: refactor
				Some(
					PurseBalance(
						None,
						dstPurse.get.id.get,
						date,
						ordering,
						dstPurseBalanceAmount.get.amount,
						dstPurseBalanceAmount.get.currency,
            true
					)
				)
			else
				None
		)
	}
}

case class ParsedRow(
	transaction: Option[Transaction],
	srcBalance: Option[PurseBalance],
	dstBalance: Option[PurseBalance]
)


class OrderingHolder {

  val Step = 1000

  val dataMap = new mutable.HashMap[String, Int]

  def getNext(key: String): Int = {
    if (!dataMap.contains(key)) {
      dataMap += (key -> 0)
    }
    dataMap += (key -> (dataMap(key) + Step))
    dataMap(key)
  }

  def reset {
    dataMap.clear()
  }
}

trait Parser[T] {
	final def parse(s: String): Option[T] = {
		s.trim match {
			case "" => None
			case nonEmpty: String => Some(doParse(nonEmpty))
		}
	}

	final def parseRequired(s: String): T = {
		s.trim match {
			case "" => throw new ParseException(s"""Not found required value, parser: """ + this.getClass)
			case nonEmpty: String => doParse(nonEmpty)
		}
	}

	protected def doParse(s: String): T
}

object DateParser extends Parser[LocalDate] {

	val datePattern = """^(\d+)\.(\d+)$""".r

	override protected def doParse(s:String): LocalDate = {
		s match {
			case datePattern(day, month) => new LocalDate(resolveYear(month.toInt), month.toInt, day.toInt)
			case _ => throw new ParseException(s"""Can not parse date "${s}" """)
		}
	}

	private def resolveYear(month: Int): Int = {
		if (month <= LocalDate.now().getMonthOfYear)
			LocalDate.now().getYear
		else
			LocalDate.now().getYear - 1
	}
}

object AmountCurrencyParser extends Parser[AmountCurrency] {

	var defaultCurrency: Currency = Currency.getInstance("RUB")

	val amountPattern = """^(-?\d+)\s*([K|К]?)\s*((?:р|\$|€|[A-Za-z]{3})?)$""".r

	def setDefaultCurrency(currency: Currency) {
		defaultCurrency = currency
	}

	override protected def doParse(s:String): AmountCurrency = s match {
		case amountPattern(amount, modifier, currencyCode) =>
			AmountCurrency(computeAmount(amount.toLong, modifier), resolveCurrency(currencyCode, defaultCurrency))

		case _ => throw new ParseException(s"""Can not parse amount "${s}" """)
	}

	private def computeAmount(amount: Long, modifier: String): Long = {
		val modifierValue =
			modifier match {
				case "K" | "К" => 1000
				case "M" | "М" => 1000000
				case "" => 1
				case _ => throw new ParseException(s"""Unexpected amount modifier value: "${modifier}" """)
			}

		amount * modifierValue
	}

	private def resolveCurrency(currencyCode: String, defaultCurrency: Currency): Currency = {
		try {
			Currency.getInstance(
				currencyCode match {
					case "$" => "USD"
					case "р" => "RUB"
					case "€" => "EUR"
					case "" => defaultCurrency.getCurrencyCode
					case other => other
				}
			)
		} catch {
			case e: IllegalArgumentException => throw new ParseException(s"""Unexpected currency code: "${currencyCode}" """)
		}
	}
}

object TypeParser extends Parser[TransactionType] {
	override protected def doParse(s:String): TransactionType =
    TransactionTypes.byCodeChain(s).getOrElse(throw new ParseException(s"""Unknown transaction type: \"${s}\" """))
}

object StringParser extends Parser[String] {
	override protected def doParse(s:String): String = s
}

object PurseParser extends Parser[Purse] {
	override protected def doParse(s:String): Purse =
    Purses.byCode(s).getOrElse(throw new ParseException(s"""Unknown purse code: "${s}" """))
}

object IntParser extends Parser[Int] {
	override protected def doParse(s:String): Int = s.toInt
}

case class AmountCurrency(amount: Long, currency: Currency)

class ParseException(m: String) extends RuntimeException(m)
