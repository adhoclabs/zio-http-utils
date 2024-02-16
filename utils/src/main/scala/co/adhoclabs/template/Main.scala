package co.adhoclabs.template

import co.adhoclabs.template.api.{ApiZ, HealthRoutes}
import com.typesafe.config.{Config, ConfigFactory}
import zio.http.Server
import zio.{ZIO, ZIOAppDefault}

import java.time.Clock

// TODO Move to unpublished project
object MainZio extends ZIOAppDefault {
  implicit val healthRoutes = HealthRoutes()

  val app = ApiZ().zioRoutes.toHttpApp
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
  val sqsConfig: Config = config.getConfig("co.adhoclabs.template.sqs")
}
