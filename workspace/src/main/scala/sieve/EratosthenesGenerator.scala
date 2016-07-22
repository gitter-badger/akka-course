package sieve

import akka.actor.{Actor, ActorRef, Props}
import sieve.Eratosthenes.{Done, FoundPrime}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Promise
import scala.util.{Success, Try}

object EratosthenesGenerator {

  def props(count: Int,
            resultPromise: Promise[Seq[Int]],
            eratosthenesProps: (Int, ActorRef) => Props = Eratosthenes.props): Props  = {
    Props(new EratosthenesGenerator(count, resultPromise, eratosthenesProps))
  }
}

class EratosthenesGenerator(
  count: Int,
  resultPromise: Promise[Seq[Int]],
  eratosthenesProps: (Int, ActorRef) => Props) extends Actor {

  val eratosthenes = context.actorOf(eratosthenesProps(count, context.self))
  val seen = ListBuffer[Int]()

  override def receive = {
    case FoundPrime(n) =>
      seen += n
    case Done =>
      resultPromise.complete(Success(seen.toList))
      context.stop(self)
    case _ =>
  }
}
