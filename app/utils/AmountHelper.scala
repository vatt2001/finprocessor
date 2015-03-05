package utils

import java.text.DecimalFormat
import models.{Purse, PurseBalance, Transaction}

class AmountHelper {
  def formatAmount(amount: Long): String = {
    new DecimalFormat("#,###,###,##0").format(amount)
  }

  def formatSrcPurseAmount(transaction: Transaction): String =
    formatTransactionPurse(transaction.srcPurse, transaction.srcPurseBalanceAfter)

  def formatDstPurseAmount(transaction: Transaction): String = transaction.dstPurse match {
    case Some(purse) => formatTransactionPurse(purse, transaction.dstPurseBalanceAfter)
    case None => ""
  }

  def formatTransactionPurse(purse: Purse, balanceAfter: Option[PurseBalance]): String = {
    purse.name + balanceAfter.map(balance => " (" + formatAmount(balance.amount) + ")").getOrElse("")
  }
}

object AmountHelper extends AmountHelper
