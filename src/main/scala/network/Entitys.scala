package network

import java.io.{BufferedReader, InputStream, OutputStream, PrintStream}
import java.net.Socket

case class User(login: String, password: String) {
  var access: Boolean = false
}

object OrderSessions extends Enumeration {
  type OrderSessions = Value
  val ALICE, BOB, TRENT = Value
}

object Commands extends Enumeration {
  type Commands = Value
  val publicKey, AB, signPublicKeyBob, signPublicKeyTrent, sessionKey = Value
}

case class Session(idSession: String, sock: Socket, bs: BufferedReader, ps: PrintStream, sessionKey: Option[Int] = null)

case class Action(source: String, destination: String, commands: List[String]) {
  override def toString: String = {
    "<" + source + ">" + "[" + destination + "]" + commands.map(command => "{" + command + "}").reduce(_ + _)
  }
}
