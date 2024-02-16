package co.adhoclabs.ziohttp

import co.adhoclabs.model.{EmptyResponse, ErrorResponse}
import zio.schema.{DeriveSchema, Schema}

case class NotFoundRequestResponse(
  error:     String,
  errorCode: Option[Int]    = None,
  contextId: Option[String] = None

)
object NotFoundRequestResponse {
  def apply(errorResponse: ErrorResponse): NotFoundRequestResponse =
    NotFoundRequestResponse(errorResponse.error, errorResponse.errorCode, errorResponse.contextId)
}

sealed trait UserFriendlyError
case class BadRequestResponse(
  error:     String,
  errorCode: Option[Int]    = None,
  contextId: Option[String] = None

) extends UserFriendlyError
object BadRequestResponse {
  def apply(errorResponse: ErrorResponse): BadRequestResponse =
    BadRequestResponse(errorResponse.error, errorResponse.errorCode, errorResponse.contextId)
}
case class InternalErrorResponse(

  error:     String,
  errorCode: Option[Int]    = None,
  contextId: Option[String] = None

) extends UserFriendlyError
object InternalErrorResponse {
  def apply(errorResponse: ErrorResponse): InternalErrorResponse =
    InternalErrorResponse(errorResponse.error, errorResponse.errorCode, errorResponse.contextId)
}

object Schemas {
  implicit val schema: Schema[EmptyResponse] = DeriveSchema.gen
  implicit val errorResponseSchema: Schema[ErrorResponse] = DeriveSchema.gen

  implicit val badRequestResponse: Schema[BadRequestResponse] = DeriveSchema.gen
  implicit val internalErrorResponse: Schema[InternalErrorResponse] = DeriveSchema.gen
  implicit val notFoundRequestResponse: Schema[NotFoundRequestResponse] = DeriveSchema.gen

}
