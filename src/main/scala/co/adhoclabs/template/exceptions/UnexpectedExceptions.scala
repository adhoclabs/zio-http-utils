package co.adhoclabs.template.exceptions

import co.adhoclabs.model.ErrorResponse

abstract class UnexpectedException(val errorResponse: ErrorResponse) extends Exception(errorResponse.error) {}

