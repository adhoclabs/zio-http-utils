package co.adhoclabs.template.api

import org.slf4j.{Logger, LoggerFactory}
import zio.http.Middleware
import zio.http.endpoint.openapi.{OpenAPIGen, SwaggerUI}

case class ApiZ(
  implicit
  healthRoute: HealthRoutes
) {
  val openApi = OpenAPIGen.fromEndpoints(
    title   = "BurnerAlbums",
    version = "1.0",
    HealthEndpoint.endpoints
  )

  val docsRoute =
    SwaggerUI.routes("docs", openApi)

  // TODO Where should this live?
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val zioRoutes = (docsRoute ++ healthRoute.routes) @@
    Middleware.requestLogging(statusCode => zio.LogLevel.Warning)

  /*    Middleware.intercept {
        (request, response) =>
          println("Submit to datadog")
          response
      }
*/
}

/*
class ApiImpl(
  implicit
  actorSystem:      ActorSystem,
  executionContext: ExecutionContext,
) {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private def logRequestResponse(request: HttpRequest, response: HttpResponse): Unit = {
    val timeout = 10.millis
    for {
      requestBodyAsBytes: ByteString <- request.entity.toStrict(timeout).map(_.data)
      responseBodyAsByes: ByteString <- response.entity.toStrict(timeout).map(_.data)
    } yield {
      val requestBodyString: String = requestBodyAsBytes.utf8String
      val responseBodyString: String = responseBodyAsByes.utf8String

      logger.info(
        (s"${response.status} " +
          s"${request.method.name} " +
          s"${request.uri} " +
          s"REQUEST BODY: $requestBodyString " +
          s"RESPONSE BODY: $responseBodyString").replace("\n", "")
      )
    }
  }

  private def logRequestRejection(request: HttpRequest, rejections: Seq[Rejection]): Unit = {
    val timeout = 10.millis
    request.entity.toStrict(timeout).map(_.data) map { requestBodyAsBytes: ByteString =>
      logger.info(
        (s"REJECTED: " +
          s"${request.method.name} " +
          s"${request.uri} " +
          s"REQUEST BODY: ${requestBodyAsBytes.utf8String} " +
          s"REJECTIONS: [${rejections.mkString(", ")}]").replace("\n", "")
      )
    }
  }

  protected def requestAndResponseLoggingHandler(request: HttpRequest): RouteResult => Unit = {
    case Complete(response)   => logRequestResponse(request, response)
    case Rejected(rejections) => logRequestRejection(request, rejections)
  }

  protected def logRequestException(exception: Exception): Unit =
    logger.error("", exception)

}

*/
