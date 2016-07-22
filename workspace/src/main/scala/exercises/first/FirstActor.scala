package exercises.first


import akka.actor.{Actor, Props}

object FirstActor {
  def props: Props = Props(new FirstActor())
}
  /*:CODEFROM:*/
class FirstActor private () extends Actor {
  def receive: Receive = {
    case x =>
    // do something with x
  }
}
  /*:CODETO:*/

  /*:SOLUTIONFROM:*/
//class FirstActor extends Actor {
//  def receive: Receive = {
//    case x =>
//      sender ! x
//  }
//}
  /*:SOLUTIONTO:*/
