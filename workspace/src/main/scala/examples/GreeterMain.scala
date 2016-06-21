package examples

import akka.actor.ActorSystem

object GreeterMain {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("MySystem")
    val greeter = system.actorOf(GreetActor.props("Hello"))
    greeter ! Greet("World!")
    greeter ! Greet("Bob!")
    Thread.sleep(1000)
    system.shutdown
  }
}
