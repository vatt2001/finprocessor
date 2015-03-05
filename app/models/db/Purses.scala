package models.db

import models._
import slick.driver.SQLiteDriver.simple._

class Purses(tag: Tag) extends IdentifiableTable[Purse](tag, "purse") {

	def typeId = column[Int]("type_id")
	def name = column[String]("name")
	def code = column[String]("code")
	def ordering = column[Int]("ordering")

	def * = (id.?, typeId, name, code, ordering) <> (Purse.tupled, Purse.unapply)
}

object Purses extends Dao[Purse, Purses] {

  import Database.dynamicSession

  lazy val dao = TableQuery[Purses]
  def sequenceName: String = "purse_id_seq"

  def byCode(code: String): Option[Purse] = dao.filter(_.code === code).firstOption
}


