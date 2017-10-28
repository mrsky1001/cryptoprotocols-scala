package protocols.mentalpoker

import java.io.IOException

import crypto.algorithms.extra.Message
import crypto.algorithms.rsa.{ConfRSA, KeysRSA, RSA}
import network._

import scala.actors.Actor.actor
import scala.util.Random

object MentalPoker {
  var networkManager: NetworkManager = _
  var mainForm: gui.MainFrame = _

  private def init(networkManager: NetworkManager, mainFrame: gui.MainFrame) {
    this.networkManager = networkManager
    this.mainForm = mainFrame
  }

  def start(whoiam: Boolean, port: Int, backlog: Int, address: String, networkManager: NetworkManager, mainFrame: gui.MainFrame): Client = {
    init(networkManager, mainFrame)
    networkManager.getUser.access = true

    var keysRSA: KeysRSA = null
    networkManager.addMessage("generate Keys")
    keysRSA = RSA.generateKeys(ConfRSA())
    networkManager.addMessage(keysRSA.toString)
    if (!whoiam) {
      val client = Connection.start(networkManager.sessions, port, backlog, address)


      val cards = (1 to 52).toList
      networkManager.addMessage("generate cards")
      networkManager.addMessage(cards.toString)
      networkManager.addMessage("Crypto and mixed cards")
      cards.map(card => RSA.encrypt(card, keysRSA.publicKey))
      networkManager.addMessage("send cards to " + OrderSessions.BOB)

      val msg = new Message(Action(client.id, OrderSessions.BOB.toString, List(Commands.CARDS.toString)) +  Random.shuffle(cards).take(52).toString())
      networkManager.sessions(0).ps.println(msg)
      actor {
        while (true) {
          for (session <- networkManager.sessions) {
            try {
              if (session.bs.ready) {
                val message = session.bs.readLine()
                val sessionName = session.idSession

                if (message.contains("<" + OrderSessions.BOB + ">")) {
                  if (message.contains("{" + Commands.SELECTEDCARDS.toString + "}")) {
                    networkManager.addMessage("get selected cards from " + sessionName)
                    val selectedCards = message.substring(message.lastIndexOf("List(") + 5, message.lastIndexOf(")")).split(", ").map(x=> x.toInt).toList
                    networkManager.addMessage("my selected cards ")
                    networkManager.addMessage(selectedCards.toString())
                  }
                  else if (message.contains("{" + Commands.SELECTED2CARDS.toString + "}")) {
                    networkManager.addMessage("get selected next cards from " + sessionName)
                    val selectedCards = message.substring(message.lastIndexOf("List(") + 5, message.lastIndexOf(")")).split(", ").map(x=> x.toInt).toList
                    networkManager.addMessage("encrypt cards ")
                    selectedCards.map(card => RSA.decrypt(card, keysRSA.privateKey))
                    networkManager.addMessage("send  cards to" + sessionName)
                    val msg2 = new Message(Action(client.id, OrderSessions.BOB.toString, List(Commands.SELECTED2CARDS.toString)) + selectedCards.toString())
                    networkManager.sessions(0).ps.println(msg2)
                  }
                }
                else networkManager.addMessage(message.substring(message.indexOf("]") + 1), flag = true)
              }
            }
            catch {
              case e: IOException => networkManager.addMessage("Error, close client!!!\n" + e.getMessage)
            }
          }
        }
      }


      client
    }

    else {
      actor {
        while (true) {
          if (networkManager.sessions.nonEmpty)
            for (source <- networkManager.sessions) {

              try {
                if (source.bs.ready) {
                  val message = source.bs.readLine()
                  val sourceName = source.idSession
                  if (message.contains("[" + OrderSessions.BOB + "]")) {
                    if (message.contains("{" + Commands.CARDS.toString + "}")) {
                      val cards = message.substring(message.lastIndexOf("List(") + 5, message.lastIndexOf(")")).split(", ").map(x=> x.toInt).toList
                      networkManager.addMessage("get cards from " + sourceName)
                      val selectCards: List[Int] = Random.shuffle(cards).take(5)
                      networkManager.addMessage("send selected cards to " + OrderSessions.ALICE.toString)
                      val msg = new Message(Action(OrderSessions.BOB.toString, OrderSessions.ALICE.toString, List(Commands.SELECTEDCARDS.toString)) + selectCards.toString())
                      networkManager.sessions(0).ps.println(msg)
                      ////
                      var selectCards2 = Random.shuffle(cards).take(5)
                      while (selectCards2.exists(card2 => selectCards.contains(card2))) {
                        selectCards2 = Random.shuffle(cards).take(5)
                      }
                      networkManager.addMessage("select next five cards ")
                      networkManager.addMessage(selectCards2.toString())
                      selectCards2.map(card => RSA.encrypt(card, keysRSA.publicKey))
                      networkManager.addMessage("encrypt and send cards to " + sourceName)
                      val msg2 = new Message(Action(OrderSessions.BOB.toString, OrderSessions.ALICE.toString, List(Commands.SELECTED2CARDS.toString)) + selectCards2.toString())
                      networkManager.sessions(0).ps.println(msg2)
                    }
                    else if (message.contains("{" + Commands.SELECTED2CARDS.toString + "}")) {
                      networkManager.addMessage("get selected next cards from " + sourceName)
                      val selectedCards = message.substring(message.lastIndexOf("List(") + 5, message.lastIndexOf(")")).split(", ").map(x=> x.toInt).toList
                      networkManager.addMessage("decrypt cards ")
                      selectedCards.map(card => RSA.decrypt(card, keysRSA.privateKey))
                      networkManager.addMessage("my selected cards ")
                      networkManager.addMessage(selectedCards.toString())
                    }
                  }
                  else if (sourceName.contains(OrderSessions.ALICE.toString)) {
                    networkManager.addMessage(message)
                  }
                }
              }
              catch {
                case e: IOException => networkManager.addMessage("Error, close client!!!\n" + e.getMessage)
              }
            }
        }
      }

      Server.start(networkManager.sessions, port, backlog, address)
      null
    }

  }

  def close(): Unit = {
    for (session <- networkManager.sessions) {
      try {
        session.bs.close()
        session.ps.close()
        session.sock.close()
      }
      catch {
        case e: Throwable => networkManager.addMessage("Error, close socket!!! \n" + e.getMessage)
      }
    }
  }
}
