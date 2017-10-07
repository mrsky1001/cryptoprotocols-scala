package network

import java.io.{BufferedReader, InputStreamReader, PrintStream}
import java.net.{InetAddress, ServerSocket}

import network.Protocol.Session

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
        val os = new PrintStream(socket.getOutputStream)
        actors.Actor.actor {
          os.println("trent")
          sessions += Session(socket, is, os, is.readLine())
        }
      }
    }
  }
}
