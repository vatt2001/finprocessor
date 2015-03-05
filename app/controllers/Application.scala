package controllers

import play.api._
import play.api.mvc._
import models.db.{Connection, PurseBalances, Transactions}
import components.Runner
import play.api.data._
import play.api.data.Forms._
import components.importer.{DataImporter, ParsedRow, DataParser}

object Application extends Controller {

	def index = Action {
    Connection.db.withDynSession {
      Ok(views.html.index(
        Transactions.getList,
        PurseBalances.getList
      ))
    }
	}

  def run = Action {
    Connection.db.withDynSession {
      Runner.run()
    }
    Ok("Done")
  }

  def upload = Action {
		Ok(views.html.upload(
      uploadForm.fill(UploadRequest("", false)),
      Seq()
    ))
	}

	def processUpload = Action { implicit request =>
    uploadForm.bindFromRequest().fold(
      errorForm => {
        Logger.error(errorForm.errorsAsJson.toString)
        BadRequest
      },
      uploadRequest => doProcessUpload(uploadRequest)
    )
	}


	def info = Action { implicit request =>
		val info = ""
		Ok(views.html.info(info))
	}

  private def doProcessUpload(uploadRequest: UploadRequest) = {
    Connection.db.withDynSession {
      if (uploadRequest.doImport) {
        new DataImporter().importData(uploadRequest.data)
      }
      Ok(views.html.upload(
        uploadForm.fill(uploadRequest),
        new DataImporter().parseData(uploadRequest.data)
      ))
    }
  }

  val uploadForm = Form(
    mapping(
      "data" -> text,
      "doImport" -> boolean
    )(UploadRequest.apply)(UploadRequest.unapply)
  )

  case class UploadRequest(data: String, doImport: Boolean)

}
