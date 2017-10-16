package network

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{ConnectException, InetAddress, Socket}

import entity.{OrderSessions, Session}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class Client(id: String, socket: Socket, is: BufferedReader, ps: PrintStream) {
  def sendMessage(message: String): Unit = {
    if (socket.isConnected) {
      if (id.equalsIgnoreCase(OrderSessions.alice.toString))
        ps.println("[" + OrderSessions.bob + "]" + message)
      else
        ps.println("[" + OrderSessions.alice + "]" + message)
    }
  }
}

object Connection {
  def start(sessions: ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session], port: Int, backlog: Int, address: String): Client = {
    try {
      val socket = new Socket(InetAddress.getByName(address), port)
      val is = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val ps = new PrintStream(socket.getOutputStream)

      val myNum = is.readLine().substring("[num] = ".length)
      val idServer = is.readLine().substring("[iam] = ".length)
      sessions += Session(idServer, socket, is, ps)

      Client(myNum, socket, is, ps)
    }
    catch {
      case e: ConnectException => {
        println("Error, can't connection!!! \n" + e.getMessage)
      }
        null
    }
  }
}