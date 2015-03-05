package components

import models._
import play.api.Logger
import models.TransactionOrBalance
import models.PurseBalance
import models.BalanceOnly
import models.BalanceDiff
import java.util.Currency

class BalanceCalculator {

  type BalanceMap = Map[Long, PurseBalance]

  def calculateBalances(rows: Seq[TransactionOrBalance], initialBalances: BalanceMap = Map): Seq[TransactionOrBalance] = {
    rows.foldLeft(FoldResult(Seq(), initialBalances)) {
      (previousResult: FoldResult, row: TransactionOrBalance) =>
        row match {
          case balanceRow: BalanceOnly =>

            val (newBalances, balanceDiffSeq) = updateBalances(previousResult.balances, balanceRow.balance)

            FoldResult(
              previousResult.resultRows ++ balanceDiffSeq ++ Seq(balanceRow),
              newBalances
            )

          case transactionRow: TransactionWithBalances =>

            var newBalances = previousResult.balances
            var newRows = previousResult.resultRows
            var newTransactionRow = transactionRow

            if (transactionRow.srcBalance.isDefined) {
              updateBalances(newBalances, transactionRow.srcBalance.get) match {
                case (balances, balanceDiffSeq) =>
                  newBalances = balances
                  newRows ++= balanceDiffSeq
              }
            } else {
              val t = transactionRow.transaction
              generateAndUpdateBalances(newBalances, t.srcPurseId, t.amount, t.currency) match {
                case (balances, balanceDiffSeq, balanceOpt) =>
                  newBalances = balances
                  newRows ++= balanceDiffSeq
                  newTransactionRow = newTransactionRow.copy(srcBalance = balanceOpt)
              }
            }

            if (transactionRow.dstBalance.isDefined) {
              updateBalances(newBalances, transactionRow.dstBalance.get) match {
                case (balances, balanceDiffSeq) =>
                  newBalances = balances
                  newRows ++= balanceDiffSeq
              }
            } else if (transactionRow.transaction.dstPurseId.isDefined){
              val t = transactionRow.transaction
              generateAndUpdateBalances(newBalances, t.dstPurseId.get, -t.amount, t.currency) match {
                case (balances, balanceDiffSeq, balanceOpt) =>
                  newBalances = balances
                  newRows ++= balanceDiffSeq
                  newTransactionRow = newTransactionRow.copy(dstBalance = balanceOpt)
              }
            }

            FoldResult(
              newRows ++ Seq(newTransactionRow),
              newBalances
            )

        }

    }.resultRows
  }

  private def updateBalances(previousBalances: BalanceMap, newBalance: PurseBalance): (BalanceMap, Seq[BalanceDiff]) = {
    val newBalances = previousBalances.updated(newBalance.purseId, newBalance)

    if (
      previousBalances.contains(newBalance.purseId)
        &&
      balanceDiffers(previousBalances(newBalance.purseId), newBalance)
    ) {
      val balanceDiff =
        BalanceDiff(
          newBalance.purseId,
          newBalance.amount - previousBalances(newBalance.purseId).amount
        )

      (newBalances, Seq(balanceDiff))
    } else {
      (newBalances, Seq())
    }
  }

  private def generateAndUpdateBalances(newBalances: BalanceMap, purseId: Long, amount: Long, currency: Currency): (BalanceMap, Seq[BalanceDiff], Option[PurseBalance]) = {
    // Сгенерить новый баланс на основе уже существующего (если есть) и вернуть новые сущности
  }

  private def balanceDiffers(currentBalance: PurseBalance, newBalance: PurseBalance): Boolean = {
    if (currentBalance.purseId != newBalance.purseId) {
      throw new RuntimeException("Balance purses does not match")
    } else if (currentBalance.currency != newBalance.currency) {
      Logger.error(s"Balance currency mismatch: found ${newBalance.currency}, expected ${currentBalance.currency}")
      return false
    } else {
      return currentBalance.amount != newBalance.amount
    }
  }

  case class FoldResult(resultRows: Seq[TransactionOrBalance], balances: BalanceMap)
}

object BalanceCalculator extends BalanceCalculator
