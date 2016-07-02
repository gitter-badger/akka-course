package sieve

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import sieve.Eratosthenes.FoundPrime
import sieve.NumberSource.Next

object Sieve {
  def props(target: ActorRef): Props = Props(new Sieve(target))
}
class Sieve private (target: ActorRef) extends Actor {

  override def receive: Receive = initial

  def initial: Receive = {
    case n: Int =>
      target ! FoundPrime(n)
      val nextRef = context.actorOf(Sieve.props(target))
      context.become(sieving(n, nextRef), discardOld = true)

  }

  def sieving(prime: Int, next: ActorRef): Receive = {
    case n: Int =>
      if(n % prime == 0)
        target ! Next
      else
        next ! n
  }
}
