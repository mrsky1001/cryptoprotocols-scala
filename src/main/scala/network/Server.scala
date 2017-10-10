package network

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{InetAddress, ServerSocket}

import gui.entity.User
import network.Protocol_2.Session

import scala.actors.Actor.actor
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Server {
  def start(sessions: ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session], first: String, second: String, third: String, port: Int, backlog: Int, address: String) {
    val serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(address))

    if (!serverSocket.isClosed)
      println("Server start...")

    actor {
      while (true) {
        val socket = serverSocket.accept()
        val is = new BufferedReader(new InputStreamReader(socket.getInputStream))
        val os = new PrintStream(socket.getOutputStream)
        val idClient = if (sessions.nonEmpty && sessions.size % 2 == 1) first else second
        actors.Actor.actor {
          os.println(third)
          sessions += Session(idClient, socket, is, os, is.readLine())
        }
      }
    }
  }
}
