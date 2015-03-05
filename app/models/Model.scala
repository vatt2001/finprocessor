package models

import models.db._

abstract class Model /* we need it just for mode nature identification */

abstract class IdentifiableModel(val identifier: Option[Long]) extends Model {

	val hasId: Boolean = identifier.nonEmpty

  def dao: Dao[IdentifiableModel, IdentifiableTable[IdentifiableModel]] = {

		import scala.reflect.runtime.universe

		val mirror = universe.runtimeMirror(getClass.getClassLoader)

		val module = mirror.staticModule(s"models.db.${getClass.getSimpleName}s")

		val obj = mirror.reflectModule(module)

		obj.instance.asInstanceOf[Dao[IdentifiableModel, IdentifiableTable[IdentifiableModel]]]
	}
}
