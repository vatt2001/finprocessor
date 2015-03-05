package models

case class Purse(
	id: Option[Long] = None,
	typeId: Int,
	name: String,
	code: String,
	ordering: Int
) extends IdentifiableModel(id)
