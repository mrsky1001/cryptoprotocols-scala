package protocols.denningsacco

import java.io.IOException
import javax.swing.JTextPane

import algorithms.rsa.RSAManager
import entity.{Session, User}
import network.{Client, Connection, Server}
import protocols.denningsacco.NumberStart.NumberStart

import scala.actors.Actor._
import scala.collection.mutable

/**
  * Created by user on 07.10.2017.
  */
object DenningSacco {

  val numberStart: NumberStart
  var messagesPane: JTextPane = _
  var mainForm: gui.MainFrame = _
  val sessions = new mutable.ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session]
  val rsaManager = new RSAManager

  def addMessage(message: String): Unit = {
    if (messagesPane != null)
      messagesPane.setText(messagesPane.getText + "\n" + message)
    else
      println("Error, messagesPane = null!!!")
  }

  def startClient(user: User, port: Int, backlog: Int, address: String, messagesPane: JTextPane, mainForm: gui.MainFrame): Client = {
    this.messagesPane = messagesPane
    this.mainForm = mainForm
    start(whoami = false, user, port, backlog, address)
  }

  def startServer(user: User, port: Int, backlog: Int, address: String, messagesPane: JTextPane, mainForm: gui.MainFrame): Client = {
    this.messagesPane = messagesPane
    this.mainForm = mainForm
    start(whoami = true, user, port, backlog, address)
  }

  /////////////////////////////////////////////////////////////////

  def start(whoami: Boolean, user: User, port: Int, backlog: Int, address: String): Client = {
    if (!whoami) {
      //client
      actor {
        while (true) {
          for (session <- sessions) {
            try {
              if (session.is.ready) {
                val message = session.is.readLine()
                val sessionName = "<" + session.loginUser + ">"

                if (message.contains("<" + third + ">")) { //if get from trent
                  if (message.contains("[openKey]")) {
                    addMessage(user.getLogin + "=> get open key from " + sessionName)
                    setOpenKey(message.substring(("<" + third + ">[openKey]").length), messagesPane)

                    generateSessionKey()
                    user.sessionKey = sessionKey
                    addMessage(user.getLogin + "=> generateSessionKey")

                    addMessage(sessionKey.toString)
                    addMessage(user.getLogin + "=> encryptSessionKey")

                    val encryptedKey = encrypt(sessionKey)
                    addMessage(encryptedKey)

                    addMessage(user.getLogin + "=> sendSessionKey to bob")
                    session.ps.println("[" + first + "][encryptedKey]" + encryptedKey)
                  }

                }
                else if (message.contains("<" + second + ">")) { //if get from alice
                  if (message.contains("[encryptedKey]")) {
                    addMessage(user.getLogin + "=> get encryptedKey from " + sessionName)

                    sessionKey = decrypt(message.substring(("<" + second + ">[encryptedKey]").length), messagesPane)
                    user.sessionKey = sessionKey
                    addMessage(user.getLogin + "=> decrypt SessionKey")
                    addMessage(sessionKey.toString)

                  }
                  else
                    addMessage(message)

                }
                else if (message.contains("<" + first + ">")) {
                  addMessage(message)
                }
              }
            }
            catch {
              case e: IOException => addMessage("Error, close client!!!\n" + e.getMessage)
            }
          }
        }
      }

      val client = Connection.start(sessions, user, port, backlog, address)
      sessions(0).ps.println(user.getLogin)

      if (first)
        addMessage(user.getLogin + "=> generateOpenKey")
      sessions(0).ps.println("[" + third + "][openKey]" + generateOpenKey)
      addMessage(openKey.toString)
      addMessage(user.getLogin + "=> send open key to " + third)

      client
    }
    else {
      actor {
        //server
        while (true) {
          if (sessions.size > 1)
            for (source <- sessions) {

              try {
                if (source.is.ready) {
                  val message = source.is.readLine()
                  val sourceName = "<" + source.id + ">" //change
                  //0 - bob, 1 - alice, x - trent
                  if (message.contains("[" + first + "]")) {
                    sessions(0).ps.println(message.replace("[" + first + "]", sourceName))
                    if (message.contains("[encryptedKey]"))
                      mainForm.dispose()
                  }
                  else if (message.contains("[" + second + "]"))
                    sessions(1).ps.println(message.replace("[" + second + "]", sourceName))
                  else if (message.contains("[" + third + "]")) {
                    if (message.contains("[openKey]")) {
                      addMessage(user.getLogin + "=> get open key from " + sourceName)
                      addMessage(user.getLogin + "=> send open key to " + second)
                      sessions(1).ps.println(message.replace("[" + third + "]", "<" + third + ">")) //send bob
                    }
                  }
                }
              }
              catch {
                case e: IOException => addMessage("Error, close client!!!\n" + e.getMessage)
              }
            }
        }
      }
      Server.start(sessions, first, second, third, port, backlog, address)
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
