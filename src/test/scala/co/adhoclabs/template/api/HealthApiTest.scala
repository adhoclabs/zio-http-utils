package co.adhoclabs.template.api

import zio.http.{Request, Status}

import scala.concurrent.Future

class HealthApiTest extends ApiTestBase {

  describe("GET /health/api") {
    it("should return a 200 response with an empty body") {
      provokeServerSuccess[String](
        app,
        Request.get(s"/health/api"),
        expectedStatus   = Status.Ok,
        payloadAssertion = _ == "API is healthy!"
      )
    }
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
