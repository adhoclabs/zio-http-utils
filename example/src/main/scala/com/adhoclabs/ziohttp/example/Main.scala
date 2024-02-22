package com.adhoclabs.ziohttp.example

import co.adhoclabs.ziohttp.utils.api.{HealthEndpoint, HealthRoutes}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}
import zio.http.endpoint.openapi.{OpenAPIGen, SwaggerUI}
import zio.http.{Middleware, Routes, Server}
import zio.{ZIO, ZIOAppDefault}

import java.time.Clock

object MainZio extends ZIOAppDefault {

  val openApi = OpenAPIGen.fromEndpoints(
    title   = "BurnerAlbums",
    version = "1.0",
    AppApi.endpoints ++ Seq(HealthEndpoint.api)
  )

  val docsRoute =
    SwaggerUI.routes("docs", openApi)

  // TODO Where should this live?
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val zioRoutes = (docsRoute ++ AppRoutes.routes ++ Routes(HealthRoutes.api)) @@
    Middleware.requestLogging(statusCode => zio.LogLevel.Warning)

  val app = zioRoutes.toHttpApp

  val config = Dependencies.config
  val host = config.getString("co.adhoclabs.template.host")
  val port = config.getInt("co.adhoclabs.template.port")
  def run = {
    ZIO.debug("Starting") *>
      Server.serve(app).provide(Server.defaultWith(config => config.binding(hostname = host, port = port))).exitCode
    /*
    TODO Can we replicate this exactly?
    Akka startup output:

      bindingFuture.onComplete {
        case Success(serverBinding) =>
          println("Starting Template with:")
          println(s"- JAVA_OPTS: ${scala.util.Properties.envOrElse("JAVA_OPTS", "<EMPTY>")}")
          println(s"Listening to ${serverBinding.localAddress}")
        case Failure(error) =>
          println(s"error: ${error.getMessage}")
      }
       */
  }
}

object Dependencies {
  // config
  implicit val config: Config = Configuration.config
  implicit val clock: Clock = Clock.systemUTC()



}

object Configuration {
  val config: Config = ConfigFactory.load
}
