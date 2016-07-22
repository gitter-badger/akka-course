package sieve

import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import sieve.Eratosthenes.{Done, FoundPrime}

class EratosthenesSpec
  extends TestKit(ActorSystem("EratosthenesSpec"))
    with DefaultTimeout with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }

  "An Eratosthenes" should {
    "Handle basic prime generation" in {
      within(500 millis) {
        val target = TestProbe()
        val e = system.actorOf(Eratosthenes.props(5,target.ref))
        target.expectMsg(FoundPrime(2))
        target.expectMsg(FoundPrime(3))
        target.expectMsg(FoundPrime(5))
        target.expectMsg(FoundPrime(7))
        target.expectMsg(FoundPrime(11))
        target.expectMsg(Done)
      }
    }
  }
}
