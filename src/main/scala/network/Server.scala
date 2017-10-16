package network

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{InetAddress, ServerSocket}

import entity.{OrderSessions, Session, User}

import scala.actors.Actor.actor
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Server {
  def start(sessions: ArrayBuffer[Session] with mutable.SynchronizedBuffer[Session], port: Int, backlog: Int, address: String) {
    val serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(address))

    if (!serverSocket.isClosed)
      println("Server start...")

    actor {
      while (true) {
        val socket = serverSocket.accept()
        val is = new BufferedReader(new InputStreamReader(socket.getInputStream))
        val ps = new PrintStream(socket.getOutputStream)
        val idClient = if (sessions.nonEmpty) OrderSessions.bob else OrderSessions.alice //&& sessions.size % 2 == 0
        actors.Actor.actor {
          ps.println("[num] = " + idClient)
          ps.println("[iam] = " + OrderSessions.trent)
          sessions += Session(idClient.toString, socket, is, ps)
        }
      }
    }
  }
}
