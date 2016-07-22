package exercises.first

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class FirstActorSpec
  extends TestKit(ActorSystem("NumberSourceSpec"))
    with DefaultTimeout with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }

  "An NumberSource" should {
    "Respond with the next integer on each request it receives" in {
      within(500 millis) {
        val ref = system.actorOf(FirstActor.props)
        ref ! "ABC"
        expectMsg(100.millis, "TESTFAIL Actor does not respond to sender with sent message", "ABC")
      }
    }
  }
}
