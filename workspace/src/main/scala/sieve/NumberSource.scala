package sieve

import akka.actor.{Actor, Props}
import sieve.NumberSource.Next

object NumberSource {
  def props(start: Int): Props = Props(new NumberSource(start))

  case object Next
}



class NumberSource private (start: Int) extends Actor {

  private var next = start
  override def receive = {
    case Next =>
      sender ! next
      next += 1
  }
}
