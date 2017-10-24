package network

import java.io.{BufferedReader, InputStream, InputStreamReader, PrintStream}
import java.net.{ConnectException, InetAddress, Socket}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class Client(id: String, socket: Socket, is: BufferedReader, ps: PrintStream) {
  def sendMessage(message: String): Unit = {
    if (socket.isConnected) {
      if (id.equalsIgnoreCase(OrderSessions.ALICE.toString))
        ps.println("[" + OrderSessions.BOB + "]" + message)
      else
        ps.println("[" + OrderSessions.ALICE + "]" + message)
    }
  }
}

object Connection {
  def start(sessions: ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session], port: Int, backlog: Int, address: String): Client = {
    try {
      val socket = new Socket(InetAddress.getByName(address), port)
      val bs = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val ps = new PrintStream(socket.getOutputStream)
      val myNum = bs.readLine().substring("[num] = ".length)
      val idServer = bs.readLine().substring("[iam] = ".length)
      sessions += Session(idServer, socket,bs, ps)

      Client(myNum, socket, bs, ps)
    }
    catch {
      case e: ConnectException => {
        println("Error, can't connection!!! \n" + e.getMessage)
      }
        null
    }
  }
}