package co.adhoclabs.ziohttp.utils.api

import zio.http.{Request, Routes, Status}

class HealthApiTest extends ApiTestBase {
  val app = Routes(HealthRoutes.api)

  describe("GET /health/api") {
    it("should return a 200 response with an empty body") {
      provokeServerSuccess[String](
        app,
        Request.get(s"/health/api"),
        expectedStatus   = Status.Ok,
        payloadAssertion = _ == "API is healthy!"
      )
    }
  }
}
