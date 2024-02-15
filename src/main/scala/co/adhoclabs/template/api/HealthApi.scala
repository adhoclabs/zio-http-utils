package co.adhoclabs.template.api

import co.adhoclabs.model.ErrorResponse
import co.adhoclabs.template.exceptions.{UnexpectedException, ValidationException}
import org.slf4j.{Logger, LoggerFactory}
import zio._
import zio.http._
import zio.http.endpoint.{Endpoint, EndpointMiddleware}
import Schemas._
import zio.http.endpoint.EndpointMiddleware.None

import scala.concurrent.Future

object HealthEndpoint {
  val okBoomer =
    ApiErrors.attachStandardErrors(
      Endpoint(Method.GET / "health" / "boom")
        .out[String]
    )

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

  val routes =
    Routes(api, okBoomer)
}
