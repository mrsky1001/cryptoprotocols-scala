package network

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{ConnectException, InetAddress, Socket}

import entity.Session

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by user on 07.10.2017.
  */
class Client(id: String, socket: Socket, is: BufferedReader, ps: PrintStream, first: String, second: String) {

  def getId: String = {
    id
  }

  def getSocket: Socket = {
    socket
  }

  def getIS: BufferedReader = {
    is
  }

  def getPS: PrintStream = {
    ps
  }

  def sendMessage(message: String): Unit = {
    if (socket.isConnected) {
      if (id.equalsIgnoreCase(second)) ps.println("[" + first + "]" + message) else ps.println("[" + second + "]" + message)
    }
  }
}

object Connection {
  def start(sessions: ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session], first: String, second: String, port: Int, backlog: Int, address: String): Client = {
    try {
      val socket = new Socket(InetAddress.getByName(address), port)
      val is = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val ps = new PrintStream(socket.getOutputStream)

      var idServer = ""
      var myId = ""
      var message = is.readLine()
      myId = message.substring("[num] = ".length)
      message = is.readLine()
      idServer = message.substring("[iam] = ".length)
      sessions += Session(idServer, socket, is, ps, idServer)
      new Client(myId, socket, is, ps, first, second)
    }
    catch {
      case e: ConnectException => {
        println("Error can't connection!!! \n" + e.getMessage)
      }
        null
    }
  }
}
