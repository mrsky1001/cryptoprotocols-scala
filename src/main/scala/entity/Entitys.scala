package entity

import java.io.{BufferedReader, PrintStream}
import java.math.BigInteger
import java.net.Socket
import protocols.denningsacco.DenningSacco.participant

/**
  * Created by user on 07.10.2017.
  */
case class User(login: String, password: String) {
  var access: Boolean = false
}

object OrderSessions extends Enumeration {
  type OrderSessions = Value
  val alice, bob, trent = Value
}

case class OpenKey(e: BigInteger, n: BigInteger, p: BigInteger, q: BigInteger) {
  override def toString: String = {
    "e:" + e + "n:" + n + "p:" + p + "q:" + q
  }
}

case class SecretKey(d: BigInteger, n: BigInteger)

case class Session(idSession: String, sock: Socket, is: BufferedReader, ps: PrintStream, sessionKey: Option[Int] = null)