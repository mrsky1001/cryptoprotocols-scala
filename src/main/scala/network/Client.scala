package network

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{ConnectException, InetAddress, Socket}

import gui.entity.User
import network.Protocol_2.Session

import scala.actors.Actor._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by user on 07.10.2017.
  */
class Client(socket: Socket, is: BufferedReader, os: PrintStream) {
  def sendMessage(message: String): Unit = {
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
  def start(sessions: ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session], port: Int, backlog: Int, address: String): Client = {
    try {
      val socket = new Socket(InetAddress.getByName(address), port)
      val is = new BufferedReader(new InputStreamReader(socket.getInputStream))
      val os = new PrintStream(socket.getOutputStream)
      actors.Actor.actor {
        val idServer = is.readLine()
        sessions += Session(idServer, socket, is, os, idServer)
      }
      new Client(socket, is, os)
    }
    catch {
      case e: ConnectException => {
        println("Error can't connection!!! \n" + e.getMessage)
      }
        null
    }
  }
}
