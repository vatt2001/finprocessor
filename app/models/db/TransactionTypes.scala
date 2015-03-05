package models.db

import models._
import slick.driver.SQLiteDriver.simple._
import play.api.db.DB

class TransactionTypes(tag: Tag) extends IdentifiableTable[TransactionType](tag, "transaction_type") {

	def parentId = column[Option[Long]]("parent_id")
	def name = column[String]("name")
	def code = column[String]("code")
	def ordering = column[Int]("ordering")

	def * = (id.?, parentId, name, code, ordering) <> (TransactionType.tupled, TransactionType.unapply)
}

object TransactionTypes extends Dao[TransactionType, TransactionTypes] {

  import Database.dynamicSession

	lazy val dao = TableQuery[TransactionTypes]
  def sequenceName: String = "transaction_type_id_seq"

  def getTopByCode(code: String): Option[TransactionType] = {
    Connection.db.withDynSession {
      dao.filter(_.code === code).filter(_.parentId.isNull).firstOption
    }
  }

  def byCodeChain(codeChain: String): Option[TransactionType] =
    Connection.db.withDynSession {
      codeChain.split("/").toList match {
        case a :: b :: nil =>
          val parent = getTopByCode(a)
          if (parent.isDefined) {
            dao.filter(_.code === b).filter(_.parentId === parent.get.id).firstOption
          } else {
            None
          }

        case a :: nil => getTopByCode(a)

        case _ =>
          throw new RuntimeException(
            s"""TransactionTypes with codes length greater than 2 is unsupported (found "$codeChain")"""
          )
      }

    }

//	def byProcessingId(id: Int): TransactionType = getOrException(dao.filter(_.processing === TransactionType(id)).firstOption)
}
