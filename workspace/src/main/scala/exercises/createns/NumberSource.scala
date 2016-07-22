package exercises.createns

import akka.actor.{Actor, Props}
import exercises.createns.NumberSource.Next

object NumberSource {
  def props(start: Int): Props = Props(new NumberSource(start))

  case object Next
}

/*:CODEFROM:*/
class NumberSource private (start: Int) extends Actor {

  override def receive = {
    case Next =>
     // put
  }
}
/*:CODETO:*/
/*:SOLUTIONFROM:*/
//class NumberSource private (start: Int) extends Actor {
//
//  // initialise the counter with the start value
//  var next = start
//
//  override def receive = {
//    case Next =>
//      sender ! next
//      next += 1
//  }
//}
/*:SOLUTIONTO:*/
