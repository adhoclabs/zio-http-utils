package com.adhoclabs.ziohttp.example

import co.adhoclabs.ziohttp.testutils.ZioHttpTestHelpers
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.funspec.AsyncFunSpec

abstract class ApiTestBase extends AsyncFunSpec with AsyncMockFactory with OneInstancePerTest with ZioHttpTestHelpers
