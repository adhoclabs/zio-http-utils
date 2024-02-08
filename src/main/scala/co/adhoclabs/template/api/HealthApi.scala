package co.adhoclabs.template.api

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
    Routes(api, okBoomer)
}
