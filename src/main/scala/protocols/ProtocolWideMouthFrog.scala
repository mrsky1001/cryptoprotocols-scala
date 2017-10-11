//package protocols
//
//import java.io.IOException
//import java.security.SecureRandom
//import javax.swing.JTextPane
//
//import algorithms.GammaJava
//import entity.{Session, User}
//
//import scala.actors.Actor._
//import scala.collection.mutable
//
///**
//  * Created by user on 07.10.2017.
//  */
//object ProtocolWideMouthFrog {
//
//  val first = "A"
//  val second = "B"
//  val third = "T"
//  val Ka = 17
//  val Kb = 59
//  var messagesPane: JTextPane = _
//  var mainForm: gui.MainFrame = _
//  val sessions = new mutable.ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session]
//  var sessionKey = 0
//  val format = new java.text.SimpleDateFormat("dd-MM-yyyy")
//
//  def generateSessionKey(): Unit = {
//    sessionKey = new SecureRandom().nextInt(500 - 10) + 1
//  }
//
//  def addMessage(message: String): Unit = {
//    if (messagesPane != null)
//      messagesPane.setText(messagesPane.getText + "\n" + message)
//    else
//      println("Error, messagesPane = null!!!")
////  }
//
//  def startClient(user: User, port: Int, backlog: Int, address: String, messagesPane: JTextPane, mainForm: gui.MainFrame): Client = {
//    this.messagesPane = messagesPane
//    this.mainForm = mainForm
//    start(whoami = false, user, port, backlog, address)
//  }
//
//  def startServer(user: User, port: Int, backlog: Int, address: String, messagesPane: JTextPane, mainForm: gui.MainFrame): Client = {
//    this.messagesPane = messagesPane
//    this.mainForm = mainForm
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
//                val message = session.is.readLine()
//                val sessionName = "<" + session.id + ">"
//
//                if (message.contains("<" + third + ">")) { //if get from trent
//                  if (message.contains("[encryptedKey]")) {
//                    val decryptedMes = session.is.readLine()
//                    addMessage(user.getLogin + "=> get encrypted mes from " + sessionName)
//                    addMessage(user.getLogin + "=> decrypt mes ")
//
//                    new GammaJava().decrypt(message.substring(("<" + third + ">[encryptedKey]").length).getBytes(), Kb)
//                    addMessage(decryptedMes)
//
//                    sessionKey = Integer.parseInt(decryptedMes.substring(35))
//                    user.sessionKey = sessionKey
//                    addMessage(user.getLogin + "=> sessionKey ")
//                    addMessage(sessionKey.toString)
//                  }
//
//                }
//                else if (message.contains("<" + second + ">")) { //if get from alice
//                  addMessage(message)
//
//                }
//                else if (message.contains("<" + first + ">")) {
//                  addMessage(message)
//                }
//              }
//            }
//            catch {
//              case e: IOException => addMessage("Error, close client!!!\n" + e.getMessage)
//            }
//          }
//        }
//      }
//      val client = Connection.start(sessions, first, second, port, backlog, address)
//      sessions(0).ps.println(user.getLogin)
//
//      if (client.getId.equals(first)) {
//        //alice
//        addMessage(user.getLogin + "=> generateSessionKey")
//        generateSessionKey()
//        user.sessionKey = sessionKey
//        addMessage(sessionKey.toString)
//        val Ta = new java.util.Date()
//        val mesA = Ta + second + sessionKey
//        addMessage(mesA)
//        addMessage(user.getLogin + "=> Encrypt(concatenation markTime +" + second + " + sessionKey)")
//        sessions(0).ps.println("[" + third + "][encryptedKey]" + first + new GammaJava().encrypt(mesA, Ka));
//        sessions(0).ps.println(Ta + second + sessionKey)
//        addMessage(user.getLogin + "=> send marks to " + third)
//      }
//      client
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
//
//                  val message = source.is.readLine()
//                  new GammaJava().encrypt(message, user.sessionKey)
//                  val sourceName = "<" + source.id + ">"
//                  //0 - alice, 1 - bob, x - trent
//                  if (message.contains("[" + first + "]")) {
//                    sessions(0).ps.println(message.replace("[" + first + "]", sourceName))
//                  }
//                  else if (message.contains("[" + second + "]"))
//                    sessions(1).ps.println(message.replace("[" + second + "]", sourceName))
//                  else if (message.contains("[" + third + "]")) {
//                    if (message.contains("[encryptedKey]")) {
//                      val decryptedMes = source.is.readLine()
//                      addMessage(user.getLogin + "=> get encrypted mes from " + sourceName)
//                      addMessage(user.getLogin + "=> decrypt mes ")
//                      new GammaJava().decrypt(message.substring(("<" + third + ">[encryptedKey]" + first).length).getBytes(), Ka)
//                      addMessage(first + decryptedMes)
//                      val sessionKey = decryptedMes.substring(35)
//                      val Tb = new java.util.Date()
//                      val mesT = Tb + first + sessionKey
//                      addMessage(mesT)
//                      addMessage(user.getLogin + "=> Encrypt(concatenation markTime + " + first + " + sessionKey)")
//                      sessions(1).ps.println("<" + third + ">[encryptedKey]" + new GammaJava().encrypt(mesT, Kb).toString);
//                      sessions(1).ps.println(Tb + second + sessionKey)
//                      addMessage(user.getLogin + "=> send marks to " + second)
//                      mainForm.dispose()
//                    }
//                  }
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
