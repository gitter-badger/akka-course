package sieve

import akka.actor.ActorSystem

object EratosthenesMain {
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem()
    actorSystem.actorOf(Eratosthenes.props(1000))
    Thread.sleep(10000)
    actorSystem.terminate()
  }
}
