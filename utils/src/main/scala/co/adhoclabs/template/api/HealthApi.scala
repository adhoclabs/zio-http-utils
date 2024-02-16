package co.adhoclabs.template.api

import zio._
import zio.http._
import zio.http.endpoint.Endpoint

object HealthEndpoint {

  val api =
    Endpoint(Method.GET / "health" / "api")
      .out[String]

  val endpoints =
    List(
      api,
    )
}

object HealthRoutes {
  val api =
    HealthEndpoint.api.implement {
      Handler.fromZIO {
        ZIO.succeed("API is healthy!")
      }
    }

  val routes =
    Routes(api)
}
