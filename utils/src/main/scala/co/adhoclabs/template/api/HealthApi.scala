package co.adhoclabs.template.api

import org.slf4j.{Logger, LoggerFactory}
import zio._
import zio.http._
import zio.http.endpoint.Endpoint

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

object HealthRoutes {
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
