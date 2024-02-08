package co.adhoclabs.template.exceptions

import co.adhoclabs.model.ErrorResponse

abstract class ValidationException(val errorResponse: ErrorResponse) extends Exception(errorResponse.error) {}

case class SongAlreadyExistsException(duplicateKeyMessage: String) extends ValidationException(
  ErrorResponse(duplicateKeyMessage)
)

case class AlbumAlreadyExistsException(duplicateKeyMessage: String) extends ValidationException(
  ErrorResponse(duplicateKeyMessage)
)

case class InvalidPatchException(invalidPatchMessage: String) extends ValidationException(
  ErrorResponse(invalidPatchMessage)
)
