import akka.actor.{ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._


class AkkaHelloWorldTest extends TestKit(ActorSystem("AkkaHelloWorldTest"))
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  val echoRef = system.actorOf(Props(new FirstActor.FirstActor))

  override def afterAll: Unit = {
    shutdown()
  }

  "An EchoActor" should {
    "Respond with the same message it receives" in {
      within(100 millis) {
        echoRef ! "test"
        expectMsg("test")
      }
    }
  }
}
