package sieve

import akka.actor.{Actor, ActorLogging, Props}
import sieve.Eratosthenes.FoundPrime
import sieve.NumberSource.Next

object Eratosthenes {
  def props(limit: Int): Props = Props(new Eratosthenes(limit))
  case class FoundPrime(prime: Int)
}
class Eratosthenes private (numberOfPrimes: Int) extends Actor with ActorLogging {

  val numberSource = context.actorOf(NumberSource.props(2),"source")
  val firstSieve = context.actorOf(Sieve.props(context.self))

  var toFind = numberOfPrimes

  override def preStart(): Unit = {
    numberSource ! Next
  }

  override def receive = {
    case Next =>
      numberSource ! Next
    case n: Int =>
      firstSieve ! n
    case FoundPrime(prime) =>
      log.info("Found prime " + prime)
      toFind -= 1
      if(toFind == 0)
        context.stop(self)
      else
        numberSource ! Next
  }
}
