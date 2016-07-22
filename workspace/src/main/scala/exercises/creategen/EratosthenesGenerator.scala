package exercises.creategen

import akka.actor.{Actor, ActorRef, Props}
import sieve.Eratosthenes
import sieve.Eratosthenes.{Done, FoundPrime}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Promise
import scala.util.Success

object EratosthenesGenerator {

  def props(count: Int,
            resultPromise: Promise[Seq[Int]],
            eratosthenesProps: (Int, ActorRef) => Props = Eratosthenes.props): Props  = {
    Props(new EratosthenesGenerator(count, resultPromise, eratosthenesProps))
  }
}

/*:CODEFROM:*/
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
  }
}
/*:CODETO:*/
/*:SOLUTIONFROM:*/
//class EratosthenesGenerator(
//                             count: Int,
//                             resultPromise: Promise[Seq[Int]],
//                             eratosthenesProps: (Int, ActorRef) => Props) extends Actor {
//
//  val eratosthenes = context.actorOf(eratosthenesProps(count, context.self))
//  val seen = ListBuffer[Int]()
//
//  override def receive = {
//    case FoundPrime(n) =>
//      seen += n
//    case Done =>
//      resultPromise.complete(Success(seen.toList))
//      context.stop(self)
//  }
//}
/*:SOLUTIONTO:*/
