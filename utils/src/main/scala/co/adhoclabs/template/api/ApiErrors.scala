package co.adhoclabs.template.api

import co.adhoclabs.template.exceptions.{UnexpectedException, ValidationException}
import co.adhoclabs.model.ErrorResponse
import co.adhoclabs.template.exceptions.{UnexpectedException, ValidationException}
import co.adhoclabs.ziohttp.Schemas._
import co.adhoclabs.ziohttp._
import org.slf4j.{Logger, LoggerFactory}
import zio._
import zio.http._
import zio.http.endpoint.{Endpoint, EndpointMiddleware}
import zio.http.endpoint.EndpointMiddleware.None
import zio.schema.codec.JsonCodec.{JsonDecoder, schemaBasedBinaryCodec}

import scala.concurrent.Future

object ApiErrors {
  def attachStandardErrors[A, B, C, D <: EndpointMiddleware](
                                                              endpoint: Endpoint[A, B, ZNothing, C, D]
                                                            ): Endpoint[A, B, Either[BadRequestResponse, InternalErrorResponse], C, D] =
    endpoint
      .outError[BadRequestResponse](Status.BadRequest)
      .outError[InternalErrorResponse](Status.InternalServerError)

  def routeWithStandardErrors[Output](future: Future[Output]) =
    Handler.fromZIO {
      ZIO.fromFuture(implicit ec => future)
        .mapError(ex => ApiErrors.exceptionHandler(ex))
    }

  def routeWithStandardErrors[Input, Output](future: Input => Future[Output]) =
    Handler.fromFunctionZIO[Input] { input =>
      ZIO.fromFuture(implicit ec => future(input))
        .mapError(ex => ApiErrors.exceptionHandler(ex))
    }

  def exceptionHandler(ex: Throwable): Either[BadRequestResponse, InternalErrorResponse] = ex match {
    case validationException: ValidationException =>
      Left(BadRequestResponse(validationException.errorResponse))
    case unexpectedException: UnexpectedException =>
      Right(InternalErrorResponse(unexpectedException.errorResponse))
    case exception: Throwable =>
      Right(InternalErrorResponse(exception.getMessage))
  }

}
