package models

import org.joda.time.{LocalDate, DateTime}
import java.util.Currency
import models.db.{PurseBalances, TransactionTypes, Purses}

case class Transaction(
	id: Option[Long] = None,
	typeId: Long,
	dateAt: LocalDate,
	ordering: Int,
	createdAt: DateTime,
	srcPurseId: Long,
	dstPurseId: Option[Long],
	amount: Long,
	currency: Currency,
	comment: String
) extends IdentifiableModel(id) {
  def withNewId = this.copy(id = Some(dao.getNextId))

  def transactionType: TransactionType = TransactionTypes.byId(typeId)
  def srcPurse: Purse = Purses.byId(srcPurseId)
  def dstPurse: Option[Purse] = Purses.byIdOption(dstPurseId)
  def srcPurseBalanceAfter: Option[PurseBalance] = PurseBalances.byPurseDateAndOrdering(srcPurseId, dateAt, ordering)
  def dstPurseBalanceAfter: Option[PurseBalance] = dstPurseId.map(PurseBalances.byPurseDateAndOrdering(_, dateAt, ordering)).getOrElse(None)
}
