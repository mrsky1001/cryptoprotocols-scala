package network

import javax.swing.text.StyledDocument

import scala.collection.mutable


class NetworkManager(user: User, messagesPane: StyledDocument) {
  val sessions = new mutable.ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session]

  def getUser: User = user

  def addMessage(message: String): Unit = {
    if (messagesPane != null)
      messagesPane.insertString(messagesPane.getLength, user.login + " => " + message + "\n", null)
    else
      println("Error, messagesPane = null!!!")
  }

}
