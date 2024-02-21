package co.adhoclabs.ziohttp.utils.exceptions

import co.adhoclabs.model.ErrorResponse

abstract class ValidationException(val errorResponse: ErrorResponse) extends Exception(errorResponse.error) {}

