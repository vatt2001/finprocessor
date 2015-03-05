package models

abstract case class TransactionOrBalance()

case class BalanceOnly(
  purseId: Long,
  balance: PurseBalance
) extends TransactionOrBalance

case class BalanceDiff(
  purseId: Long,
  amount: Long
) extends TransactionOrBalance

case class TransactionWithBalances(
  transaction: Transaction,
  srcBalance: Option[PurseBalance],
  dstBalance: Option[PurseBalance]
) extends TransactionOrBalance
