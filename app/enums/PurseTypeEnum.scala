package enums

class PurseTypeEnum extends Enumeration {
	type PurseTypeValue = Value

  val Current = Value(1)
  val Savings = Value(2)
  val Investment = Value(3)
  val IncomeOrExpense = Value(4)
}
