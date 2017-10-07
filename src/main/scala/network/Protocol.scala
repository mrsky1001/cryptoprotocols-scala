package network

import java.io.{BufferedReader, PrintStream}
import java.net.Socket
import javax.swing.JTextPane

import scala.actors.Actor._
import scala.collection.mutable

/**
  * Created by user on 07.10.2017.
  */
object Protocol {

  case class Session(sock: Socket, is: BufferedReader, ps: PrintStream, name: String)

  var messagesPane: JTextPane = _

  def addMessage(message: String): Unit = {
    if (messagesPane != null)
      messagesPane.setText(messagesPane.getText + "\n" + message)
    else
      println("Error, messagesPane = null!!!")
  }

  def startOptionClient(messagePane: JTextPane): Client = {
    this.messagesPane = messagesPane
    start(whoami = false)
  }

  def startOptionServer(messagePane: JTextPane): Client = {
    this.messagesPane = messagesPane
    start(whoami = true)
  }

  def startClient(port: Int, backlog: Int, address: String, messagesPane: JTextPane): Client = {
    this.messagesPane = messagesPane
    start(whoami = false, port, backlog, address)
  }

  def startServer(port: Int, backlog: Int, address: String, messagesPane: JTextPane): Client = {
    this.messagesPane = messagesPane
    start(whoami = true, port, backlog, address)
  }

  def start(whoami: Boolean, port: Int = 8080, backlog: Int = 3, address: String = "localhost"): Client = {
    val sessions = new mutable.ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session]
    if (!whoami)
      Connection.start(port, backlog, address)
    else {
      Server.start(sessions, port, backlog, address)

      actor {
        while (true) {
          for (session <- sessions) {
            if (session.is.ready) {
              val input = session.is.readLine()
              for (user2 <- sessions) {
                user2.ps.println(session.name + " : " + input)
                addMessage(session.name + ": " + input)
              }
            }
          }
        }
      }
      null
    }
  }
}
