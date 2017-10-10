package entity

import java.io.{BufferedReader, PrintStream}
import java.net.Socket

/**
  * Created by user on 07.10.2017.
  */
class User(login: String, password: String) {
  var sessionKey: Int = 0

  def getLogin: String = {
    login
  }

  def getPassword: String = {
    password
  }

  def setSessionKey(sessionKey: Int): Unit = {
    this.sessionKey = sessionKey
  }
}

case class Session(id: String, sock: Socket, is: BufferedReader, ps: PrintStream, loginUser: String)