package network

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{ConnectException, InetAddress, Socket}

import scala.actors.Actor._

/**
  * Created by user on 07.10.2017.
  */
class Client(socket: Socket, is: BufferedReader, os: PrintStream) {
  def sendMesage(message: String): Unit = {
    if (socket.isConnected) {
      val myActor =
        actor {
          receive {
            case msg => os.println(msg)
          }
        }
      myActor ! message
    }
  }
}

object Connection {
  def start(port: Int, backlog: Int, address: String): Client = {
    try {
      val socket = new Socket(InetAddress.getByName(address), port)
      val is = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val os = new PrintStream(socket.getOutputStream)
      new Client(socket, is, os)
    }

    catch {
      case e: ConnectException => {
        println("Error-connection" + e.getMessage)
      }
        null
    }
  }
}
