package co.adhoclabs.template.api

import co.adhoclabs.template.TestBase
import co.adhoclabs.ziohttp.testutils.ZioHttpTestHelpers

abstract class ApiTestBase extends TestBase with ZioHttpTestHelpers {

  // ZIO-http bits
  implicit val healthRoutes = HealthRoutes()

  val zioRoutes = ApiZ().zioRoutes
  val app = zioRoutes.toHttpApp

}
