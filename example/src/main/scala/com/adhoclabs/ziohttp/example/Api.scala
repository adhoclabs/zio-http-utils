package com.adhoclabs.ziohttp.example

import co.adhoclabs.ziohttp.utils.api.{ApiErrors, HealthEndpoint}
import org.slf4j.{Logger, LoggerFactory}
import zio._
import zio.http._
import zio.http.endpoint.Endpoint

object AppApi {
  val okBoomer =
    ApiErrors.attachStandardErrors(
      Endpoint(Method.GET / "health" / "boom")
        .out[String]
    )

  val endpoints =
    List(
      okBoomer
    )
}

object AppRoutes {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val okBoomer =
    AppApi.okBoomer.implement {
      Handler.fromZIO {
        ZIO.attempt(???)
          .mapError(ex => ApiErrors.exceptionHandler(ex))
      }
    }

  val routes =
    Routes(okBoomer)
}
