package examples

import akka.actor.{Actor, ActorLogging}

class HelloActor extends Actor with ActorLogging {

  def receive = {
    case Greet(name) => log.info(s"Hello $name")
    case _      => log.info("received unknown message")
  }
}
