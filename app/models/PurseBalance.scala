package models

import org.joda.time.LocalDate
import java.util.Currency

case class PurseBalance(
	id: Option[Long] = None,
	purseId: Long,
	dateAt: LocalDate,
	ordering: Int,
	amount: Long,
	currency: Currency,
	isConfirmed: Boolean
) extends IdentifiableModel(id) {
  def withNewId = this.copy(id = Some(dao.getNextId))
}
