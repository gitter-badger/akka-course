package exercises.createsieve

import akka.actor.{Actor, ActorRef, Props}
import sieve.Eratosthenes.FoundPrime
import sieve.NumberSource.Next

object Sieve {
  def props(target: ActorRef): Props = props(target, props)
  def props(target: ActorRef, childProps: (ActorRef) => Props): Props = Props(new Sieve(target, childProps))
}

/*:CODEFROM:*/
class Sieve private (target: ActorRef, childProps: (ActorRef) => Props) extends Actor {

  override def receive: Receive = initial

  def initial: Receive = {
    case n: Int =>
      ???
  }

  def sieving(prime: Int, next: ActorRef): Receive = {
    case n: Int =>
      ???
  }
}
/*:CODETO:*/
/*SOLUTIONFROM:*/
//class Sieve private (target: ActorRef, childProps: (ActorRef) => Props) extends Actor {
//
//  override def receive: Receive = initial
//
//  def initial: Receive = {
//    case n: Int =>
//      target ! FoundPrime(n)
//      val nextRef = context.actorOf(childProps(target))
//      context.become(sieving(n, nextRef), discardOld = true)
//  }
//
//  def sieving(prime: Int, next: ActorRef): Receive = {
//    case n: Int =>
//      if (n % prime == 0)
//        target ! Next
//      else
//        next ! n
//  }
//}
/*SOLUTIONTO:*/
