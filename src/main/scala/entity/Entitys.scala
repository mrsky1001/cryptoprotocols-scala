package entity

import java.io.{BufferedReader, PrintStream}
import java.net.Socket

/**
  * Created by user on 07.10.2017.
  */
case class User(login: String, password: String, access: Boolean)

case class NamesSession(first: String, second: String, third: String)

case class Session(nameSession: String, num: String, sock: Socket, is: BufferedReader, ps: PrintStream, sessionKey: Option[Int] = None)