package models.db

import play.api.Play.current
import play.api.db._

import models.IdentifiableModel
import slick.driver.SQLiteDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}

object Connection {

	lazy val db = Database.forDataSource(DB.getDataSource())

	def dynamic[T](pointcut: => T) = db.withDynTransaction(pointcut)
}

trait DbConnected {

	def doWithinTransaction[T](pointcut: => T): T = {
		Connection.dynamic(pointcut)
	}
}

abstract class IdentifiableTable[M <: IdentifiableModel](tag: Tag, table: String) extends Table[M](tag, table) {

//	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def id = column[Long]("id", O.PrimaryKey) // SQLite does not support proper autoincrement? TODO: Proper cross-database autoincrement
}

abstract class Dao[M <: IdentifiableModel, T <: IdentifiableTable[M]] {

	val dao: TableQuery[T]

  def sequenceName: String

//  def getNextId = (Q[Long] + s"""select nextval('"${sequenceName}"')""").first

  def getNextId: Long = getMaxId + 1

  def getMaxId: Long = dao.map(_.id).max.run.getOrElse(0)

  def byId(id: Long): M = getOrException(byIdOption(id))

	def byIdOption(id: Option[Long]): Option[M] = id.flatMap(byIdOption)

	def byIdOption(id: Long) = dao.filter(_.id === id).firstOption

  def getList: Seq[M] = dao.sortBy(_.id).list()

	def insertAndGet(model: M): M = byId(insert(model))

//	def insert(model: M): Long = (dao.returning(dao.map(_.id)) += model)
	def insert(model: M): Long = (dao += model)

	def update(model: M): Long = {
		dao.filter(_.id === model.identifier)
			.update(model)
	}

	def take(model: M) = {
		if (model.hasId) {
			update(model)
		} else {
			insert(model)
		}
	}

	protected def getOrException(option: Option[M]) = option.getOrElse(
		throw new ObjectNotFoundException(s"query result is empty (db: ${this.getClass.getSimpleName})")
	)
}
