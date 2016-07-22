package sieve

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import sieve.Eratosthenes.{Done, FoundPrime}
import sieve.NumberSource.Next

object Eratosthenes {
  case class FoundPrime(prime: Int)
  case object Done

  def props(limit: Int, target: ActorRef): Props = {
    Props(new Eratosthenes(limit, target))
  }

  def props(limit: Int, target: ActorRef,
            numberSourceProps: Int => Props = NumberSource.props,
            sieveProps: ActorRef => Props = Sieve.props
           ): Props = Props(new Eratosthenes(limit, target, numberSourceProps, sieveProps))
}

class Eratosthenes private (
  numberOfPrimes: Int,
  target: ActorRef,
  numberSourceProps: Int => Props = NumberSource.props,
  sieveProps: ActorRef => Props = Sieve.props
  ) extends Actor with ActorLogging {

  val numberSource = context.actorOf(numberSourceProps(2),"source")
  val firstSieve = context.actorOf(sieveProps(context.self))

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
      target ! FoundPrime(prime)
      toFind -= 1
      if(toFind == 0) {
        target ! Done
        context.stop(self)
      } else
        numberSource ! Next
  }
}
