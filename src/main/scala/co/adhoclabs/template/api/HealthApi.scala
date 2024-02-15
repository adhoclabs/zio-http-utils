package co.adhoclabs.template.api

import co.adhoclabs.model.ErrorResponse
import co.adhoclabs.template.exceptions.{UnexpectedException, ValidationException}
import org.slf4j.{Logger, LoggerFactory}
import zio._
import zio.http._
import zio.http.endpoint.Endpoint
import Schemas._

import scala.concurrent.Future

object HealthEndpoint {
  val okBoomer =
    Endpoint(Method.GET / "health" / "boom")
      .out[String]
      .outError[BadRequestResponse](Status.BadRequest)
      .outError[InternalErrorResponse](Status.InternalServerError)

  val api =
    Endpoint(Method.GET / "health" / "api")
      .out[String]

  val endpoints =
    List(
      api,
      okBoomer
    )
}

case class HealthRoutes() {
  val api =
    HealthEndpoint.api.implement {
      Handler.fromZIO {
        ZIO.succeed("API is healthy!")
      }
    }

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val okBoomer =
    HealthEndpoint.okBoomer.implement {
      Handler.fromZIO {
        ZIO.attempt(???)
          .mapError(ex => ApiErrors.exceptionHandler(ex))
      }
    }

  def mapRouteErrors[Output](future: Future[Output]) =
    Handler.fromZIO {
      ZIO.fromFuture(implicit ec => future)
        .mapError(ex => ApiErrors.exceptionHandler(ex))
    }

  val routes =
    Routes(api, okBoomer)
}
