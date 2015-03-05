import play.Project._
import sbt._
import Keys._
import Tests._

/**
 * This is a simple sbt setup generating Slick code from the given
 * database before compiling the projects code.
 */
object Build extends Build {
	lazy val mainProject = play.Project(
		"finprocessor",
		applicationVersion = "1.0.0",
		path = file("."),
		settings = playScalaSettings ++ Seq(
			scalaVersion := "2.10.3",
			libraryDependencies ++= List(
				jdbc,
				anorm,
				cache,
				"org.xerial" % "sqlite-jdbc" % "3.8.7",
//				"org.slf4j" % "slf4j-nop" % "1.6.4",
				"com.typesafe.slick" %% "slick" % "2.0.1"
//				"com.typesafe.slick" %% "slick" % "2.1.0"
//				"com.typesafe.slick" %% "slick-codegen" % "2.1.0-RC3"
			)
//			slick <<= slickCodeGenTask // register manual sbt command
//			sourceGenerators in Compile <+= slickCodeGenTask // register automatic code generation on every compile, remove for only manual use
		)
	)

//	// code generation task
//	lazy val slick = TaskKey[Seq[File]]("gen-tables")
//	lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
//		val outputDir = (dir / "slick").getPath // place generated files in sbt's managed sources folder
//	val url = "jdbc:sqlite:db/default.db"
//		val jdbcDriver = "org.sqlite.JDBC"
//		val slickDriver = "scala.slick.driver.SQLiteDriver"
//		val pkg = "default"
//		toError(r.run("scala.slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg), s.log))
//		val fname = outputDir + "/default/Tables.scala"
//		Seq(file(fname))
//	}
}
