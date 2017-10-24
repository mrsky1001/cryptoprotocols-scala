package network

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{InetAddress, ServerSocket}

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
        val bs = new BufferedReader(new InputStreamReader(socket.getInputStream))
        val ps = new PrintStream(socket.getOutputStream)
        val idClient = if (sessions.nonEmpty) OrderSessions.BOB else OrderSessions.ALICE
        actors.Actor.actor {
          ps.println("[num] = " + idClient)
          ps.println("[iam] = " + OrderSessions.TRENT)

          sessions += Session(idClient.toString, socket, bs, ps)
        }
      }
    }
  }
}
