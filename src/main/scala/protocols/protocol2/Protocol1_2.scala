//package protocols.protocol2
//
//import java.io.IOException
//
//import crypto.algorithms.extra._
//import crypto.algorithms.rsa.{ConfRSA, KeysRSA, RSA}
//import network._
//import protocols.extra.Protocol
//
//import scala.actors.Actor._
//
//object Protocol1_2 extends Protocol with ExtraFunc {
//
//  var networkManager: NetworkManager = _
//  var mainForm: gui.MainFrame = _
//
//  private def init(networkManager: NetworkManager, mainFrame: gui.MainFrame) {
//    this.networkManager = networkManager
//    this.mainForm = mainFrame
//  }
//
//  def start(whoiam: Boolean, port: Int, backlog: Int, address: String, networkManager: NetworkManager, mainFrame: gui.MainFrame): Client = {
//    init(networkManager, mainFrame)
//
//    if (!whoiam) {
//      var keysRSA: KeysRSA = null
//      val client = Connection.start(networkManager.sessions, port, backlog, address)
//      if (client.id.equalsIgnoreCase("bob")) {
//        networkManager.addMessage("generate Keys")
//
//        keysRSA = RSA.generateKeys(ConfRSA())
//        networkManager.addMessage(keysRSA.toString)
//
//        networkManager.addMessage("send publicKey to " + OrderSessions.TRENT)
//        val msg = new Message(Action(client.id, OrderSessions.TRENT.toString, List(Commands.publicKey.toString)) + keysRSA.publicKey.toString)
//        networkManager.sessions(0).ps.println(msg)
//      }
//      var publicKeyBob: PublicKey = null
//      var sessionKey: SessionKey = null
//      actor {
//        while (true) {
//          for (session <- networkManager.sessions) {
//            try {
//              if (session.bs.ready) {
//                val message = session.bs.readLine()
//                val sessionName = session.idSession
//
//                if (message.contains("<" + OrderSessions.TRENT + ">")) {
//                  if (message.contains("{" + Commands.publicKey.toString + "}")) {
//                    networkManager.addMessage("get open key from " + sessionName)
//                    publicKeyBob = parseKey(message).asInstanceOf[PublicKey]
//                    networkManager.addMessage("generate session key ")
//                    sessionKey = SessionKey(random(1, 99))
//                    networkManager.addMessage("sessionkey " + sessionKey.toString)
//                    networkManager.addMessage("send sessionkey to " + OrderSessions.BOB.toString)
//                    RSA.encrypt(Array(sessionKey.key.toByte), publicKeyBob)
//                    val msg = new Message(Action(client.id, OrderSessions.BOB.toString, List(Commands.sessionKey.toString)).toString + "sk:" + sessionKey.key)
//                    networkManager.sessions(0).ps.println(msg)
//                  }
//                  networkManager.getUser.access = true
//                  networkManager.addMessage("Access, true!")
//
//                }
//                else if (message.contains("<" + OrderSessions.ALICE + ">") && client.id.equalsIgnoreCase("bob")) {
//                  if (message.contains("{" + Commands.sessionKey.toString + "}")) {
//                    networkManager.addMessage("get sessionKey key from " + OrderSessions.ALICE)
//                    sessionKey = SessionKey(message.substring(message.indexOf("sk:") + 3).toInt)
//                    RSA.decrypt(message.getBytes(), keysRSA.privateKey)
//                    networkManager.addMessage("sessionkey " + sessionKey.toString)
//                  }
//                  networkManager.getUser.access = true
//                  networkManager.addMessage("Access, true!")
//
//                }
//                else networkManager.addMessage(message.substring(message.indexOf("]") + 1), flag = true)
//              }
//            }
//            catch {
//              case e: IOException => networkManager.addMessage("Error, close client!!!\n" + e.getMessage)
//            }
//          }
//        }
//      }
//
//
//      client
//    }
//
//    else {
//      actor {
//        while (true) {
//          if (networkManager.sessions.nonEmpty)
//            for (source <- networkManager.sessions) {
//
//              try {
//                if (source.bs.ready) {
//                  val message = source.bs.readLine()
//                  val sourceName = source.idSession //change
//                  //0 - bob, 1 - alice, x - trent
//                  //[] - who destination? <> - who source
//                  if (message.contains("[" + OrderSessions.TRENT + "]")) {
//                    if (message.contains("{" + Commands.publicKey.toString + "}")) {
//                      val getPublicKey = parseKey(message).asInstanceOf[PublicKey]
//                      networkManager.addMessage("get open key from " + sourceName)
//                      networkManager.addMessage("send open key to " + OrderSessions.ALICE.toString)
//                      val msg2 = new Message(Action(OrderSessions.TRENT.toString, OrderSessions.ALICE.toString, List(Commands.publicKey.toString)) + getPublicKey.toString)
//                      networkManager.sessions(0).ps.println(msg2)
//                    }
//                    mainFrame.dispose()
//                  }
//                  else if (sourceName.contains(OrderSessions.BOB.toString)) {
//                    networkManager.sessions(0).ps.println(message)
//                  }
//                  else if (sourceName.contains(OrderSessions.ALICE.toString)) {
//                    networkManager.sessions(1).ps.println(message)
//                  }
//                }
//              }
//              catch {
//                case e: IOException => networkManager.addMessage("Error, close client!!!\n" + e.getMessage)
//              }
//            }
//        }
//      }
//
//      Server.start(networkManager.sessions, port, backlog, address)
//      null
//    }
//
//  }
//
//  def close(): Unit = {
//    for (session <- networkManager.sessions) {
//      try {
//        session.bs.close()
//        session.ps.close()
//        session.sock.close()
//      }
//      catch {
//        case e: Throwable => networkManager.addMessage("Error, close socket!!! \n" + e.getMessage)
//      }
//    }
//  }
//}