package exercises.createsieve

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import sieve.Eratosthenes.FoundPrime
import sieve.NumberSource.Next

import scala.concurrent.duration._

class SieveSpec
  extends TestKit(ActorSystem("NumberSourceSpec"))
    with DefaultTimeout with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }

  case class NewChild(target: ActorRef)

  class TestChild(output: ActorRef, target: ActorRef) extends Actor {
    override def preStart: Unit = {
      output ! NewChild(target)
    }
    override def receive = {
      case x =>
        output ! x
    }
  }

  "An Sieve" should {
    "Respond with the next integer on each request it receives" in {
      within(500 millis) {
        val sieveTarget = TestProbe()
        val childTarget = TestProbe()
        val sieve = system.actorOf(Sieve.props(sieveTarget.ref, t => Props(new TestChild(childTarget.ref, t))))

        // send in first value - sieve should declare this a prime.
        sieve ! 2
        sieveTarget.expectMsg(FoundPrime(2))
        childTarget.expectMsg(NewChild(sieveTarget.ref))

        sieve ! 3
        sieveTarget.expectNoMsg(50.milli)
        childTarget.expectMsg(3)

        sieve ! 4
        sieveTarget.expectMsg(Next)
        childTarget.expectNoMsg(50.milli)
      }
    }
  }
}
