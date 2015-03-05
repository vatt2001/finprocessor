package models.db

import models._
import slick.driver.SQLiteDriver.simple._
import org.joda.time.LocalDate
import java.util.Currency

class PurseBalances(tag: Tag) extends IdentifiableTable[PurseBalance](tag, "purse_balance") {

	def purseId = column[Long]("purse_id")
	def dateAt = column[LocalDate]("date_at")
	def ordering = column[Int]("ordering")
	def amount = column[Long]("amount")
	def currency = column[Currency]("currency_id")
	def isConfirmed = column[Boolean]("is_confirmed")

	def * = (id.?, purseId, dateAt, ordering, amount, currency, isConfirmed) <> (PurseBalance.tupled, PurseBalance.unapply)
}

object PurseBalances extends Dao[PurseBalance, PurseBalances] {
  import Database.dynamicSession

  lazy val dao = TableQuery[PurseBalances]
  def sequenceName: String = "purse_balance_id_seq"

  def byPurseDateAndOrdering(purseId: Long, dateAt: LocalDate, ordering: Int): Option[PurseBalance] = {
    dao.filter(_.purseId === purseId).filter(_.dateAt === dateAt).filter(_.ordering === ordering).firstOption
  }
}

