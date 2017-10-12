package network

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{ConnectException, InetAddress, Socket}

import entity.{NamesSession, Session}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by user on 07.10.2017.
  */
class Client(nameClient: String, namesSession: NamesSession, socket: Socket, is: BufferedReader, ps: PrintStream) {
  def sendMessage(message: String): Unit = {
    if (socket.isConnected) {
      ps.println("[" + (if (nameClient.equalsIgnoreCase(namesSession.second)) namesSession.first else namesSession.second) + "]" + message)
    }
  }
}

object Connection {
  def start(sessions: ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session], namesSession: NamesSession, port: Int, backlog: Int, address: String): Client = {
    try {
      val socket = new Socket(InetAddress.getByName(address), port)
      val is = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val ps = new PrintStream(socket.getOutputStream)

      val myNum = is.readLine().substring("[num] = ".length)
      val idServer = is.readLine().substring("[iam] = ".length)
      sessions += Session(idServer, myNum, socket, is, ps)

      new Client(myNum, namesSession, socket, is, ps)
    }
    catch {
      case e: ConnectException => {
        println("Error, can't connection!!! \n" + e.getMessage)
      }
        null
    }
  }
}
