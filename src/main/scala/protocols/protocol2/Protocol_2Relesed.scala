//package protocols
//
//import java.io.IOException
//import java.math.BigInteger
//import java.security.SecureRandom
//import javax.swing.JTextPane
//
//import algorithms.rsa.RSAJava
//import entity.{Session, User}
//import network.{Client, Connection, Server}
//
//import scala.actors.Actor._
//import scala.collection.mutable
//
///**
//  * Created by user on 07.10.2017.
//  */
//object Protocol_2Relesed {
//
//  val first = "bob"
//  val second = "alice"
//  val third = "trent"
//  var messagesPane: JTextPane = _
//  var mainForm: gui.MainFrame = _
//  val sessions = new mutable.ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session]
//
//  val sizePQ: Int = 5
//  var rsa: RSAJava = new RSAJava(sizePQ)
//
//  var openKey = 0
//  var sessionKey = 0
//
//  def generateOpenKey: String = {
//    openKey = rsa.getN.intValue
//    rsa.getE.toString + "n:" + rsa.getN.toString + "p:" + rsa.getP.toString + "q:" + rsa.getQ
//  }
//
//  def generateSessionKey(): Unit = {
//    sessionKey = new SecureRandom().nextInt(openKey - 10) + 1
//  }
//
//  def setOpenKey(message: String, textPane: JTextPane): Unit = {
//    val indexN = message.indexOf("n:")
//    val indexP = message.indexOf("p:")
//    val indexQ = message.indexOf("q:")
//
//    val e = new BigInteger(message.substring(0, indexN))
//    val n = new BigInteger(message.substring(indexN + 2, indexP))
//    val p = new BigInteger(message.substring(indexP + 2, indexQ))
//    val q = new BigInteger(message.substring(indexQ + 2))
//
//    System.out.println("e, n, p ,q ")
//    System.out.println(e + " " + n + " " + p + " " + q)
//    rsa = new RSAJava(sizePQ, e, n, p, q, textPane)
//    openKey = n.intValue
//  }
//
//  def encrypt(key: Int): String = rsa.encrypt(key)
//
//  def decrypt(str: String, jTextPane: JTextPane): Int = rsa.decrypt(new BigInteger(str), jTextPane)
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
//    if (!whoami) {
//      //client
//      actor {
//        while (true) {
//          for (session <- sessions) {
//            try {
//              if (session.is.ready) {
//                val message = session.is.readLine()
//                val sessionName = "<" + session.loginUser + ">"
//
//                if (message.contains("<" + third + ">")) { //if get from trent
//                  if (message.contains("[openKey]")) {
//                    addMessage(user.getLogin + "=> get open key from " + sessionName)
//                    setOpenKey(message.substring(("<" + third + ">[openKey]").length), messagesPane)
//
//                    generateSessionKey()
//                    user.sessionKey = sessionKey
//                    addMessage(user.getLogin + "=> generateSessionKey")
//
//                    addMessage(sessionKey.toString)
//                    addMessage(user.getLogin + "=> encryptSessionKey")
//
//                    val encryptedKey = encrypt(sessionKey)
//                    addMessage(encryptedKey)
//
//                    addMessage(user.getLogin + "=> sendSessionKey to bob")
//                    session.ps.println("[" + first + "][encryptedKey]" + encryptedKey)
//                  }
//
//                }
//                else if (message.contains("<" + second + ">")) { //if get from alice
//                  if (message.contains("[encryptedKey]")) {
//                    addMessage(user.getLogin + "=> get encryptedKey from " + sessionName)
//
//                    sessionKey = decrypt(message.substring(("<" + second + ">[encryptedKey]").length), messagesPane)
//                    user.sessionKey = sessionKey
//                    addMessage(user.getLogin + "=> decrypt SessionKey")
//                    addMessage(sessionKey.toString)
//
//                  }
//                  else
//                    addMessage(message)
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
//      if (client.getId.equals(first)) { //if bob
//        addMessage(user.getLogin + "=> generateOpenKey")
//        sessions(0).ps.println("[" + third + "][openKey]" + generateOpenKey)
//        addMessage(openKey.toString)
//        addMessage(user.getLogin + "=> send open key to " + third)
//
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
//                  val message = source.is.readLine()
//                  val sourceName = "<" + source.id + ">"//change
//                  //0 - bob, 1 - alice, x - trent
//                  if (message.contains("[" + first + "]")) {
//                    sessions(0).ps.println(message.replace("[" + first + "]", sourceName))
//                    if (message.contains("[encryptedKey]"))
//                        mainForm.dispose()
//                  }
//                  else if (message.contains("[" + second + "]"))
//                    sessions(1).ps.println(message.replace("[" + second + "]", sourceName))
//                  else if (message.contains("[" + third + "]")) {
//                    if (message.contains("[openKey]")) {
//                      addMessage(user.getLogin + "=> get open key from " + sourceName)
//                      addMessage(user.getLogin + "=> send open key to " + second)
//                      sessions(1).ps.println(message.replace("[" + third + "]", "<" + third + ">")) //send bob
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
