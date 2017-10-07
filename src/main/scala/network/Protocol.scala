package network

import java.io.{BufferedReader, IOException, PrintStream}
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
  val sessions = new mutable.ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session]

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
    if (!whoami) {
      actor {
        while (true) {
          for (session <- sessions) {
            try {
              if (session.is.ready) {
                val input = session.is.readLine()
                addMessage(input)
              }
            }
            catch {
              case e: IOException => addMessage("Error, close client!!!\n" + e.getMessage)
            }
          }
        }
      }
      Connection.start(sessions, port, backlog, address)
    }
    else {
      actor {
        while (true) {
          if (sessions.size > 1)
            for (source <- sessions) {

              try {
                if (source.is.ready) {
                  val input = source.is.readLine()
                  val message = source.name + ": " + input

                  if (message.contains("[alice]"))
                    sessions.find(s => s.name.equalsIgnoreCase("alice")).get.ps.println(message.replace("[alice]", ""))
                  else if (message.contains("[bob]"))
                    sessions.find(s => s.name.equalsIgnoreCase("bob")).get.ps.println(message.replace("[bob]", ""))
                  else if (message.contains("[trent]"))
                    addMessage(message.replace("[trent]", ""))
                }
              }
              catch {
                case e: IOException => addMessage("Error, close client!!!\n" + e.getMessage)
              }
            }
        }
      }
      Server.start(sessions, port, backlog, address)
      null
    }
  }

  def close(): Unit = {
    for (session <- sessions) {
      try {
        session.is.close()
        session.ps.close()
        session.sock.close()
      }
      catch {
        case e: Throwable => addMessage("Error, close socket!!! \n" + e.getMessage)
      }
    }
  }
}
