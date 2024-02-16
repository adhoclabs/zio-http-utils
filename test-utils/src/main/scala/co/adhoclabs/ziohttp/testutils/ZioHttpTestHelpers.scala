package co.adhoclabs.ziohttp.testutils

import co.adhoclabs.model.ErrorResponse
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.funspec.AsyncFunSpec
import zio.{Exit, Unsafe, ZIO}
import zio.http.{HttpApp, Request, Status}
import zio.schema.Schema
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec

trait ZioHttpTestHelpers extends AsyncFunSpec with AsyncMockFactory with OneInstancePerTest {
  def provokeServerFailure(
    app:            HttpApp[Any],
    request:        Request,
    expectedStatus: Status,
    errorAssertion: ErrorResponse => Boolean = _ => true
  ) = {
    import co.adhoclabs.ziohttp.Schemas._

    val (status, errorResponse) =
      invokeZioRequest[ErrorResponse](app, request)
        .left
        .getOrElse(throw new Exception("Broken failure test!"))

    assert(status == expectedStatus)
    assert(errorAssertion(errorResponse))
  }

  def provokeServerSuccess[T: Schema](
    app:              HttpApp[Any],
    request:          Request,
    expectedStatus:   Status,
    payloadAssertion: T => Boolean = (_: T) => true
  ) = {
    val (status, errorResponse) =
      invokeZioRequest(app, request)
        .right
        .getOrElse(throw new Exception("Broken successful test!"))
    assert(status == expectedStatus)
    assert(payloadAssertion(errorResponse))
  }

  private def invokeZioRequest[T: Schema](app: HttpApp[Any], request: Request): Either[(Status, ErrorResponse), (Status, T)] = {
    val runtime = zio.Runtime.default
    Unsafe.unsafe { implicit unsafe =>

      import co.adhoclabs.ziohttp.Schemas._
      runtime.unsafe.run {
        (for {
          response <- app.apply(request)
          _ <- ZIO.when(response.status.isError)(
            for {
              errorResponse <- response.body.to[ErrorResponse]
            } yield ZIO.fail((response.status, errorResponse))
          )
          res <- response.body.to[T]
        } yield (response.status, res))
          .mapError {
            case (errorStatus: Status, er: ErrorResponse) =>
              (errorStatus, er)
            case other =>
              (Status.InternalServerError, ErrorResponse(other.toString))
          }
      }
    } match {
      case Exit.Success((status, value)) =>
        value match {
          case er: ErrorResponse =>
            Left((status, er))
          case other =>
            Right((status, value))
        }
      case other =>
        other match {
          case Exit.Success(value) => Right(value)
          case Exit.Failure(cause) =>
            Left(cause.failureOrCause.left.getOrElse(???))
        }
    }
  }

}
