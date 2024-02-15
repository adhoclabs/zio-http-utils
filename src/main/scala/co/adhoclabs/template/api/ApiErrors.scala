package co.adhoclabs.template.api

import co.adhoclabs.template.exceptions.{UnexpectedException, ValidationException}

object ApiErrors {
  def exceptionHandler(ex: Throwable): Either[BadRequestResponse, InternalErrorResponse] = ex match {
    case validationException: ValidationException =>
      Left(BadRequestResponse(validationException.errorResponse))
    case unexpectedException: UnexpectedException =>
      Right(InternalErrorResponse(unexpectedException.errorResponse))
    case exception: Throwable =>
      Right(InternalErrorResponse(exception.getMessage))
  }

}
