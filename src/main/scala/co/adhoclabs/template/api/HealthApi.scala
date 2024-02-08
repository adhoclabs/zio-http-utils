package co.adhoclabs.template.api

import co.adhoclabs.model.ErrorResponse
import co.adhoclabs.template.business.HealthManager
import org.slf4j.{Logger, LoggerFactory}
import zio._
import zio.http._
import zio.http.endpoint.Endpoint

object HealthEndpoint {
  val okBoomer =
    Endpoint(Method.GET / "health" / "boom")
      .out[String]

  val api =
    Endpoint(Method.GET / "health" / "api")
      .out[String]

  import Schemas.errorResponseSchema
  val db =
    Endpoint(Method.GET / "health" / "db")
      .out[String]
      .outError[ErrorResponse](Status.InternalServerError)

  val endpoints =
    List(
      api,
      db,
      okBoomer
    )
}

case class HealthRoutes(implicit healthManager: HealthManager) {
  val api =
    HealthEndpoint.api.implement {
      Handler.fromZIO {
        ZIO.succeed("API is healthy!")
      }
    }

  val db =
    HealthEndpoint.db.implement {
      Handler.fromZIO {
        ZIO.fromFuture(
          implicit ec =>
            healthManager.executeDbGet()
        ).map(_ => "DB is healthy!")
          .mapError { throwable =>
            println("Throwable in health check: " + throwable)
            ErrorResponse(throwable.getMessage)
          }
      }
    }

  val logger: Logger = LoggerFactory.getLogger(this.getClass)
  val okBoomer =
    HealthEndpoint.okBoomer.implement {
      Handler.fromZIO {
        ZIO.succeed(???)
      }.catchAllDefect(defect =>
        Handler.from(
          ZIO.succeed(
            logger.error("", defect)
          ) *>
            ZIO.logError(defect.toString) *>
            ZIO.die(defect)
        ))
    }

  val routes =
    Routes(api, db, okBoomer)
}
