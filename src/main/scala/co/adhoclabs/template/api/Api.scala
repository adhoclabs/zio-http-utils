package co.adhoclabs.template.api

import co.adhoclabs.model.ErrorResponse
import co.adhoclabs.template.exceptions.{UnexpectedException, ValidationException}
import org.slf4j.{Logger, LoggerFactory}
import zio.http.endpoint.Endpoint
import zio.{Cause, ZIO}
import zio.http.endpoint.openapi.{OpenAPIGen, SwaggerUI}
import zio.http.{Body, Handler, Middleware, Response, RoutePattern, Routes, Status}

case class ApiZ(
  implicit
  albumApiZ:   AlbumRoutes,
  songRoutes:  SongRoutes,
  healthRoute: HealthRoutes
) {
  val openApi = OpenAPIGen.fromEndpoints(
    title   = "BurnerAlbums",
    version = "1.0",
    SongApiEndpoints.endpoints ++ AlbumEndpoints.endpoints ++ HealthEndpoint.endpoints
  )

  val docsRoute =
    SwaggerUI.routes("docs", openApi)

  val unhandled =
    Routes(
      Endpoint(RoutePattern.any)
        .out[String]
        .implement(
          Handler.fromFunctionZIO(
            _ =>
              ZIO.debug("Unhandled.").as("Unhandled")
          )
        )
    )

  // TODO Where should this live?
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  val zioRoutes: Routes[Any, Nothing] = (docsRoute ++ healthRoute.routes ++ albumApiZ.routes ++ songRoutes.routes ++ unhandled)
    .handleErrorCauseZIO { cause =>
      import Schemas.errorResponseSchema
      import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
      println("In handleErrorCause")
      cause match {
        case f: Cause.Fail[Response] =>
          for {
            _ <- ZIO.logErrorCause("Big Fail", f)

            bodyString <- f.value.body.asString
              .orDie
            _ <- ZIO.logError(bodyString)
          } yield f.value.copy(body = Body.from(ErrorResponse("Did you pass the wrong URL into the mocked call?" + bodyString)))

        case Cause.Die(value, trace) =>
          value match {
            case validationException: ValidationException =>
              println("ValidationException: " + validationException)
              ZIO.succeed(
                Response(Status.BadRequest, body = Body.from(validationException.errorResponse))
              )
            case unexpectedException: UnexpectedException =>
              println("Unexpected: " + unexpectedException)
              ZIO.succeed(
                Response(Status.InternalServerError, body = Body.from(unexpectedException.errorResponse))
              )
            case exception: Exception =>
              logger.error("", exception)
              println("", exception)
              ZIO.succeed(
                Response(Status.InternalServerError, body = Body.from(ErrorResponse("Exception. " + exception.getMessage)))
              )
            case rawThrowable =>
              ZIO.succeed(
                Response(Status.BadRequest, body = Body.from(ErrorResponse(rawThrowable.getMessage)))
              )
          }
        case interrupt: Cause.Interrupt =>
          ZIO.succeed(
            Response(Status.InternalServerError, body = Body.from(ErrorResponse("Process Interrupted. " + interrupt)))
          )
        case other =>
          println("Other: " + other)

          ZIO.succeed(
            Response(Status.Forbidden, body = Body.from(ErrorResponse(other.prettyPrint)))
          )

      }

    }
    .mapErrorZIO(errResponse =>
      ZIO.debug("ErrorResponse: " + errResponse).as(errResponse)) @@
    Middleware.requestLogging(statusCode => zio.LogLevel.Warning)
  //    Middleware.debug

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

  protected val exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case validationException: ValidationException =>
        logRequestException(validationException)
        complete(StatusCodes.BadRequest -> validationException.errorResponse)
      case unexpectedException: UnexpectedException =>
        logRequestException(unexpectedException)
        complete(StatusCodes.InternalServerError -> unexpectedException.errorResponse)
      case exception: Exception =>
        logRequestException(exception)
        complete(StatusCodes.InternalServerError -> exception.getMessage)
    }
}

*/
