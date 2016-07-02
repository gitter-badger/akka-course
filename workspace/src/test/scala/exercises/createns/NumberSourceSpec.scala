package exercises.createns

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class NumberSourceSpec
  extends TestKit(ActorSystem("NumberSourceSpec"))
    with DefaultTimeout with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }

  "An NumberSource" should {
    "Respond with the next integer on each request it receives" in {
      within(500 millis) {
        val numberSource = system.actorOf(NumberSource.props(10))
        numberSource ! NumberSource.Next
        expectMsg(50.millis, "TESTFAIL First call to counter does not start with given initial value", 10)
        numberSource ! NumberSource.Next
        expectMsg(50.millis, "TESTFAIL Next does not return next in sequence",11)
        numberSource ! NumberSource.Next
        expectMsg(50.millis, "TESTFAIL Next does not return next in sequence",12)
      }
    }
  }
}
