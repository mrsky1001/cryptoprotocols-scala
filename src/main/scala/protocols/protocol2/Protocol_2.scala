//package protocols.protocol2
//
//import java.io.IOException
//import javax.swing.JTextPane
//
//import entity.{Session, User}
//import network.{Client, Connection, Server}
//
//import scala.actors.Actor._
//import scala.collection.mutable
//
///**
//  * Created by user on 07.10.2017.
//  */
//object Protocol_2 {
//
//  val first = "bob"
//  val second = "alice"
//  val third = "trent"
//  var messagesPane: JTextPane = _
//  val sessions = new mutable.ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session]
//
//  def addMessage(message: String): Unit = {
//    if (messagesPane != null)
//      messagesPane.setText(messagesPane.getText + "\n" + message)
//    else
//      println("Error, messagesPane = null!!!")
//  }
//
//  def startClient(user: User, port: Int, backlog: Int, address: String, messagesPane: JTextPane): Client = {
//    this.messagesPane = messagesPane
//    start(whoami = false, user, port, backlog, address)
//  }
//
//  def startServer(user: User, port: Int, backlog: Int, address: String, messagesPane: JTextPane): Client = {
//    this.messagesPane = messagesPane
//    start(whoami = true, user, port, backlog, address)
//  }
//
//  def start(whoami: Boolean, user: User, port: Int, backlog: Int, address: String): Client = {
//    if (!whoami) {
//      //client
//      actor {
//        while (true) {
//          for (session <- sessions) {
//            try {
//              if (session.is.ready) {
//                val input = session.is.readLine()
//                addMessage(input)
//              }
//            }
//            catch {
//              case e: IOException => addMessage("Error, close client!!!\n" + e.getMessage)
//            }
//          }
//        }
//      }
//      Connection.start(sessions,first, second, port, backlog, address)
//    }
//    else {
//      actor {
//        //server
//        while (true) {
//          if (sessions.size > 1)
//            for (source <- sessions) {
//
//              try {
//                if (source.is.ready) {
//                  val message = source.is.readLine()
//                  val sourceName = "<" + source.loginUser + ">"
//
//                  if (message.contains("["+first+"]"))
//                    sessions(0).ps.println(message.replace("["+first+"]", sourceName))
//                  else if (message.contains("["+second+"]"))
//                    sessions(1).ps.println(message.replace("["+second+"]", sourceName))
//                  else if (message.contains("["+third+"]"))
//                    addMessage(message.replace("["+third+"]", sourceName))
//                }
//              }
//              catch {
//                case e: IOException => addMessage("Error, close client!!!\n" + e.getMessage)
//              }
//            }
//        }
//      }
//      Server.start(sessions, first, second, third, port, backlog, address)
//      null
//    }
//  }
//
//  def close(): Unit = {
//    for (session <- sessions) {
//      try {
//        session.is.close()
//        session.ps.close()
//        session.sock.close()
//      }
//      catch {
//        case e: Throwable => addMessage("Error, close socket!!! \n" + e.getMessage)
//      }
//    }
//  }
//}
