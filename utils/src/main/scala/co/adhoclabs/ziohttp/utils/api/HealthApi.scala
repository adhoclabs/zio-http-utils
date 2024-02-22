package co.adhoclabs.ziohttp.utils.api

import zio._
import zio.http._
import zio.http.endpoint.Endpoint

import scala.concurrent.Future

object HealthEndpoint {

  val api =
    ApiErrors.attachStandardErrors(
      Endpoint(Method.GET / "health" / "api")
        .out[String]
    )
}

object HealthRoutes {
  val api =
    HealthEndpoint.api.implement {
      ApiErrors.routeWithStandardErrors(
        (_: Unit) => Future.successful("API is healthy!")
      )
    }
}
