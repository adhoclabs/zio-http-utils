package com.adhoclabs.ziohttp.example

import zio.http.{Request, Status}

class HealthApiTest extends ApiTestBase {
  val app = AppRoutes.routes

  describe("GET /health/api") {
    it("should return a 500 ErrrorResponse when a defect is encountered") {
      provokeServerFailure(
        app,
        Request.get(s"/health/boom"),
        expectedStatus   = Status.InternalServerError,
        errorAssertion = _.error == "an implementation is missing"
      )
    }
  }


}
