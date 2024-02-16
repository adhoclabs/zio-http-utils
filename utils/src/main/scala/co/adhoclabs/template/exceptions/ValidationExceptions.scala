package co.adhoclabs.template.exceptions

import co.adhoclabs.model.ErrorResponse

abstract class ValidationException(val errorResponse: ErrorResponse) extends Exception(errorResponse.error) {}

