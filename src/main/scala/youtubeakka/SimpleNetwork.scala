package youtubeakka

import java.io.{BufferedReader, InputStream, InputStreamReader, PrintStream}
import java.net.{InetAddress, ServerSocket, Socket}

import collection.mutable
import scala.actors.Actor
import scala.actors.Actor._

/**
  * Created by user on 07.10.2017.
  */
object SimpleNetwork {

  case class User(sock: Socket, is: BufferedReader, ps: PrintStream, name: String)

  def main(args: Array[String]): Unit = {
    val port = 8080
    val backlog = 3
    val address = "localhost"
    val users = new mutable.ArrayBuffer[User] with mutable.SynchronizedBuffer[User]
    val server = new ServerSocket(port, backlog, InetAddress.getByName(address))

    actor {
      while (true) {
        val sock = server.accept()
        val is = new BufferedReader(new InputStreamReader(sock.getInputStream()))
        val os = new PrintStream(sock.getOutputStream())
        actors.Actor.actor {
          os.println("Name pleas: ")
          users += User(sock, is, os, is.readLine())
        }
      }
    }

    while (true) {
      for (user <- users) {
        if (user.is.ready) {
          val input = user.is.readLine()
          for (user2 <- users) {
            user2.ps.println(user.name + " : " + input)
          }
        }
      }
    }
  }
}
