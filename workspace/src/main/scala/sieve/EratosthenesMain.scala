package sieve

import akka.actor.ActorSystem
import scala.concurrent.duration._

import scala.concurrent.{Await, Promise}

object EratosthenesMain {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem()
    try {
      val promise = Promise[Seq[Int]]()
      system.actorOf(EratosthenesGenerator.props(1, promise))
      val answer = Await.result(promise.future, 10.seconds)
      println("Primes = " + answer)
    } finally {
      system.terminate()
    }
  }
}
