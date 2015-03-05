package models

import models.db.TransactionTypes

case class TransactionType(
	id: Option[Long] = None,
	parent: Option[Long] = None,
	name: String,
	code: String,
	ordering: Int
) extends IdentifiableModel(id) {
  def formattedName: String = parent match {
    case Some(id) => TransactionTypes.byId(id).formattedName + "/" + name
    case None => name
  }
}
