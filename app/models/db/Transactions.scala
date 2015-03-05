package models.db

import models._
import slick.driver.SQLiteDriver.simple._
import org.joda.time.{LocalDate, DateTime}
import java.util.Currency

class Transactions(tag: Tag) extends IdentifiableTable[Transaction](tag, "transaction") {

	def typeId = column[Long]("type_id")
	def dateAt = column[LocalDate]("date_at")
  def ordering = column[Int]("ordering")
	def createdAt = column[DateTime]("created_at")
	def srcPurseId = column[Long]("src_purse_id")
	def dstPurseId = column[Option[Long]]("dst_purse_id")
	def amount = column[Long]("amount")
	def currency = column[Currency]("currency_id")
	def comment = column[String]("comment")

	def * = (id.?, typeId, dateAt, ordering, createdAt, srcPurseId, dstPurseId, amount, currency, comment) <> (Transaction.tupled, Transaction.unapply)
}

object Transactions extends Dao[Transaction, Transactions] {
  lazy val dao = TableQuery[Transactions]
  def sequenceName: String = "transaction_id_seq"
}
