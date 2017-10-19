//package protocols.denningsacco
//
//import java.io.IOException
//import java.math.BigInteger
//import java.util.Date
//import javax.swing.JTextPane
//
//import algorithms.CRC
//import RSA.RSAManager
//import entity._
//import network.{Client, Connection, Server}
//
//import scala.actors.Actor._
//import scala.collection.mutable
//
///**
//  * Created by user on 07.10.2017.
//  */
//object DenningSacco {
//
//  var messagesPane: JTextPane = _
//  var mainForm: gui.MainFrame = _
//  val sessions = new mutable.ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session]
//
//  var openKey: OpenKey = _
//  var secretKey: SecretKey = _
//  var sessionKey: Int = _
//  val timeEq: Long = 100000
//  var openKeyAlice: OpenKey = _
//  var openKeyBob: OpenKey = _
//  var openKeyTrent: OpenKey = _
//  val participant = new Participant(new RSAManager)
//  var secretKeyAlice: SecretKey = _
//  var secretKeyBob: SecretKey = _
//  var secretKeyTrent: SecretKey = _
//
//  def addMessage(message: String): Unit = {
//    if (messagesPane != null)
//      messagesPane.setText(messagesPane.getText + "\n" + message)
//    else
//      println("Error, messagesPane = null!!!")
//  }
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
//
//
//    if (!whoami) {
//      val client = Connection.start(sessions, port, backlog, address)
//      addMessage(user.login + " => generateOpenKey")
//      openKey = participant.getOpenKey
//      addMessage("openkey " + openKey.toString)
//      addMessage(user.login + " => generateSecretKey")
//      secretKey = participant.getSecretKey
//      addMessage("secretkey " + secretKey.toString)
//
//      addMessage(user.login + " => sendOpenKey to " + OrderSessions.trent)
//      sessions(0).ps.println("[" + OrderSessions.trent + "][openKey]" + openKey.toString)
//      //client
//      actor {
//        while (true) {
//          for (session <- sessions) {
//            try {
//              if (session.is.ready) {
//                val message = session.is.readLine()
//                val sessionName = "<" + session.idSession + ">"
//
//                if (message.contains("<" + OrderSessions.alice + ">") && client.id.equalsIgnoreCase(OrderSessions.bob.toString)) {
//                  if (message.contains("[sessionKey]")) {
//                    val getMesStr = message.replace("<" + OrderSessions.alice + ">[sessionKey]", "")
//                    addMessage(user.login + " => get E(S(K, Ta)), S(B, Kb), S(A, Ka) from " + OrderSessions.alice)
//
//                    openKeyBob = participant.parseOpenKey(getMesStr.substring(getMesStr.indexOf("kb:")))
//                    addMessage(user.login + " => get open key Kb " + OrderSessions.alice + " from " + session.idSession)
//                    addMessage(openKeyBob.e.toString())
//
//                    val Ta = getMesStr.substring(getMesStr.indexOf("ta:") + 3, getMesStr.indexOf("ka:")).toLong + 10000
//                    addMessage(user.login + " => get Ta from " + session.idSession)
//                    addMessage(Ta.toString)
//
//                    if (openKey.e.intValue() != openKeyBob.e.intValue() || new Date().getTime - Ta > timeEq) {
//                      addMessage("Error, incorrect CRC or time-error!!!")
//                    } else {
//                      openKeyAlice = participant.parseOpenKey(getMesStr.substring(0, getMesStr.indexOf("kb")))
//                      addMessage(user.login + " => get open key Ka " + OrderSessions.alice + " from " + session.idSession)
//                      addMessage(openKeyAlice.e.toString())
//
//                      sessionKey = getMesStr.substring(getMesStr.indexOf("sk:") + 3, getMesStr.indexOf("ta:")).toInt
//                      addMessage(user.login + " => get session key from " + session.idSession)
//                      addMessage(sessionKey.toString)
//                      user.access = true
//                    }
//                  }
//                  else
//                    addMessage(message)
//
//                }
//                else if (message.contains("<" + OrderSessions.bob + ">")&& client.id.equalsIgnoreCase(OrderSessions.alice.toString) ) {
//                  addMessage(message)
//                }
//                else if (message.contains("<" + OrderSessions.trent + ">")) {
//                  if (message.contains("[openKey]")) {
//                    val getOpenKey = participant.parseOpenKey(message.replace("<" + OrderSessions.trent + ">[openKey]", ""))
//                    openKeyTrent = getOpenKey
//
//                    if (client.id.equalsIgnoreCase(OrderSessions.alice.toString)) {
//                      addMessage(user.login + " => get open key from " + session.idSession)
//                      addMessage(user.login + " => send {A, B} to " + session.idSession)
//                      sessions(0).ps.println("[" + OrderSessions.trent + "][A, B]")
//                    }
//                  }
//                  else if (message.contains("[signKey1]")) {
//                    val getMesStr = message.replace("<" + OrderSessions.trent + ">[signKey1]", "")
//                    println("e = " + CRC.unsign(openKey, openKeyTrent, getMesStr.substring(0, getMesStr.indexOf("e:"))))
//
//                    openKeyBob = participant.parseOpenKey(getMesStr)
//                    addMessage(user.login + " => get CRC open key " + OrderSessions.bob + " from " + session.idSession)
//                    addMessage(openKeyBob.e.toString())
//                  }
//                  else if (message.contains("[signKey2]")) {
//                    val getMesStr = message.replace("<" + OrderSessions.trent + ">[signKey2]", "")
//                    println("e = " + CRC.unsign(openKey, openKeyTrent, getMesStr.substring(0, getMesStr.indexOf("e:"))))
//
//                    openKeyTrent = participant.parseOpenKey(getMesStr)
//                    addMessage(user.login + " => get CRC open key " + OrderSessions.trent + " from " + session.idSession)
//                    addMessage(openKeyTrent.e.toString())
//
//                    /////
//                    addMessage(user.login + " => generate session key ")
//                    sessionKey = participant.generateSessionKey(openKey)
//                    addMessage("sessionkey " + sessionKey.toString)
//
//                    addMessage(user.login + " => generate mark Ta ")
//                    val markTa = new Date().getTime
//                    addMessage(new Date().toString)
//                    val signedK = CRC.sign(openKey, openKeyBob, new BigInteger(sessionKey.toString))
//                    val signedKb = CRC.sign(openKey, openKeyBob, new BigInteger(openKeyBob.e.toString))
//                    val signedKa = CRC.sign(openKey, openKeyBob, new BigInteger(openKey.e.toString))
//
//                    addMessage(user.login + " => send E(S(K, Ta)), S(B, Kb), S(A, Ka) to " + OrderSessions.bob)
//                    sessions(0).ps.println("[" + OrderSessions.bob + "][sessionKey]" + "sk:" + sessionKey + "ta:" + markTa + "ka:" + openKey.toString + "kb:" + openKeyBob.toString)
//                    user.access = true
//                  }
//                }
//              }
//            }
//            catch {
//              case e: IOException => addMessage("Error, close client!!!\n" + e.getMessage)
//            }
//          }
//        }
//      }
//
//
//      client
//    }
//
//    else {
//      actor {
//        while (true) {
//          if (sessions.size > 1)
//            for (source <- sessions) {
//
//              try {
//                if (source.is.ready) {
//                  val message = source.is.readLine()
//                  val sourceName = "<" + source.idSession + ">" //change
//                  //0 - bob, 1 - alice, x - trent
//                  //[] - who destination? <> - who source
//                  if (message.contains("[" + OrderSessions.alice + "]")) {
//                    sessions(0).ps.println(message.replace("[" + OrderSessions.alice + "]", sourceName))
//                  }
//                  else if (message.contains("[" + OrderSessions.bob + "]")) {
//                    sessions(1).ps.println(message.replace("[" + OrderSessions.bob + "]", sourceName))
//                  }
//                  else if (message.contains("[" + OrderSessions.trent + "]")) {
//                    if (message.contains("[openKey]")) {
//                      val getOpenKey = participant.parseOpenKey(message.replace("[" + OrderSessions.trent + "][openKey]", ""))
//
//                      addMessage(user.login + " => get open key from " + source.idSession)
//                      addMessage(user.login + " => send my open key to " + source.idSession)
//                      if (source.idSession.equalsIgnoreCase(OrderSessions.alice.toString)) {
//                        addMessage(user.login + " => generateOpenKey")
//                        openKey = participant.getOpenKey
//                        addMessage("openKey " + openKey.toString)
//
//                        addMessage(user.login + " => generateSecretKey")
//                        secretKey = participant.getSecretKey
//                        addMessage("secretkey " + secretKey.toString)
//
//                        openKeyAlice = getOpenKey
//                        sessions(0).ps.println("<" + OrderSessions.trent + ">[openKey]" + openKey.toString)
//                      } else {
//                        openKeyBob = getOpenKey
//                        sessions(1).ps.println("<" + OrderSessions.trent + ">[openKey]" + openKey.toString)
//                      }
//                    }
//                    else if (message.contains("[A, B]")) {
//                      addMessage(user.login + " => get A, B from " + source.idSession)
//                      addMessage(user.login + " => send S(B, Kb) from " + source.idSession)
//                      sessions(0).ps.println("<" + OrderSessions.trent + ">[signKey1]" + CRC.sign(openKey, openKeyAlice, new BigInteger(openKeyBob.e.toString)) + openKeyBob.toString)
//                      sessions(0).ps.println("<" + OrderSessions.trent + ">[signKey2]" + CRC.sign(openKey, openKeyAlice, new BigInteger(openKey.e.toString)) + openKey.toString)
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
//
//      Server.start(sessions, port, backlog, address)
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
