package co.adhoclabs.template

import java.util.Random

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.OneInstancePerTest
import org.scalatest.funspec.AsyncFunSpec

abstract class TestBase extends AsyncFunSpec with AsyncMockFactory with OneInstancePerTest {
  protected val random = new Random()
  protected implicit val testClock = new TestClock()


}
