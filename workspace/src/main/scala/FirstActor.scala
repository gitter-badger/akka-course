
import akka.actor.Actor

object FirstActor {

  /*:CODEFROM:*/
/**
 * An Actor that echoes everything you send to it
 */
//class FirstActor extends Actor {
//  def receive: Receive = {
//    case x =>
//    // do something with x
//  }
//}
  /*:CODETO:*/

  /*:SOLUTIONFROM:*/
class FirstActor extends Actor {
  def receive: Receive = {
    case x =>
      sender ! x
  }
}
  /*:SOLUTIONTO:*/
}
