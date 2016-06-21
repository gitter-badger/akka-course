package examples

import akka.actor.{Actor, ActorLogging, Props}



class GreetActor private (greeting: String) extends Actor with ActorLogging {

  def receive = {
    case Greet(name) => log.info(s"$greeting $name")
    case _      => log.info("received unknown message")
  }
}

object GreetActor {
  def props(greeting: String): Props =
    Props(new GreetActor(greeting))
}
