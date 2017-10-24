package protocols.denningsacco

import java.io.IOException
import java.util.Date

import crypto.algorithms.eds.EDSrsa
import crypto.algorithms.extra._
import crypto.algorithms.rsa.{ConfRSA, KeysRSA, RSA}
import network._
import protocols.extra.Protocol

import scala.actors.Actor._

object DenningSacco extends Protocol with ExtraFunc {

  var networkManager: NetworkManager = _
  var mainForm: gui.MainFrame = _

  private def init(networkManager: NetworkManager, mainFrame: gui.MainFrame) {
    this.networkManager = networkManager
    this.mainForm = mainFrame
  }

  def start(whoiam: Boolean, port: Int, backlog: Int, address: String, networkManager: NetworkManager, mainFrame: gui.MainFrame): Client = {
    init(networkManager, mainFrame)

    if (!whoiam) {
      val client = Connection.start(networkManager.sessions, port, backlog, address)
      networkManager.addMessage("generate Keys")

      val keysRSA = RSA.generateKeys(ConfRSA())
      networkManager.addMessage(keysRSA.toString)

      networkManager.addMessage("send publicKey to " + OrderSessions.TRENT)
      val msg = new Message(Action(client.id, OrderSessions.TRENT.toString, List(Commands.publicKey.toString)) + keysRSA.publicKey.toString)
      networkManager.sessions(0).ps.println(msg)

      var publicKeyTrent: PublicKey = null
      var publicKeyBob: PublicKey = null
      var publicKeyAlice: PublicKey = null
      var sessionKey: SessionKey = null
      var signedBobMSG: Array[Byte] = null
      var signedTrentMSG: Array[Byte] = null
      actor {
        while (true) {
          for (session <- networkManager.sessions) {
            try {
              if (session.bs.ready) {
                val message = session.bs.readLine()
                val sessionName = session.idSession

                if (message.contains("<" + OrderSessions.ALICE.toString + ">") && client.id.equalsIgnoreCase(OrderSessions.BOB.toString)) {
                  if (message.contains("{" + Commands.sessionKey.toString + "}")) {
                    networkManager.addMessage("get E(S(K, Ta)), S(B, Kb), S(A, Ka) from " + OrderSessions.ALICE.toString)

                    sessionKey = SessionKey(message.substring(message.indexOf("sk:") + 3, message.indexOf("ta:")).toInt)
                    networkManager.addMessage("get session key " + OrderSessions.ALICE.toString + " from " + OrderSessions.BOB.toString)
                    networkManager.addMessage(SessionKey.toString())

                    publicKeyAlice = parseKey(message).asInstanceOf[PublicKey]
                    networkManager.addMessage("get open key Ka " + OrderSessions.ALICE.toString + " from " + OrderSessions.BOB.toString)
                    networkManager.addMessage(publicKeyAlice.toString())

                    publicKeyBob = parseKey(message.substring(message.lastIndexOf("kb:") + 3)).asInstanceOf[PublicKey]
                    networkManager.addMessage("get open key Kb " + OrderSessions.ALICE.toString + " from " + OrderSessions.BOB.toString)
                    networkManager.addMessage(publicKeyBob.toString())

                    val Ta = message.substring(message.indexOf("ta:") + 3, message.indexOf("ka:")).toLong + 10000
                    networkManager.addMessage("get Ta from " + sessionName)
                    networkManager.addMessage(Ta.toString)
                    if (new Date().getTime - Ta > 500000) {
                      networkManager.addMessage("Error, time-error!!!")
                    } else
                      networkManager.addMessage("Check  time, OK!")

                    val signedSessionMSG = new Message(session.bs.readLine()).getBytes
                    if (!EDSrsa.verification(Array(sessionKey.key.toByte), signedSessionMSG, keysRSA.privateKey, publicKeyAlice)) {
                      networkManager.addMessage("Error, incorrect sign!!!")
                    } else
                      networkManager.addMessage("Verification, OK!")

                    val signedKbMSG = new Message(session.bs.readLine()).getBytes
                    if (!EDSrsa.verification(new Message(keysRSA.publicKey.toString).getBytes, signedKbMSG, keysRSA.privateKey, publicKeyAlice)) {
                      networkManager.addMessage("Error, incorrect sign!!!")
                    } else
                      networkManager.addMessage("Verification, OK!")

                    val signedKaMSG = new Message(session.bs.readLine()).getBytes
                    if (!EDSrsa.verification(new Message(publicKeyAlice.toString).getBytes, signedKaMSG, keysRSA.privateKey, publicKeyAlice)) {
                      networkManager.addMessage("Error, incorrect sign!!!")
                    }
                    else
                      networkManager.addMessage("Verification, OK!")


                    networkManager.getUser.access = true
                    networkManager.addMessage("Access, true!")
                  }


                }
                else if (message.contains("<" + OrderSessions.BOB.toString + ">") && client.id.equalsIgnoreCase(OrderSessions.ALICE.toString.toString)) {
                  networkManager.addMessage(message)
                }
                else if (message.contains("<" + OrderSessions.TRENT + ">")) {
                  if (message.contains("{" + Commands.publicKey.toString + "}")) {
                    networkManager.addMessage("get open key from " + sessionName)
                    publicKeyTrent = parseKey(message).asInstanceOf[PublicKey]

                    if (client.id.equalsIgnoreCase(OrderSessions.ALICE.toString)) {
                      networkManager.addMessage("send {A, B} to " + sessionName)
                      val msg = new Message(Action(client.id, OrderSessions.TRENT.toString, List(Commands.AB.toString)).toString)
                      networkManager.sessions(0).ps.println(msg)
                    }
                  }
                  else if (message.contains("{" + Commands.signPublicKeyBob.toString + "}") && client.id.equals(OrderSessions.ALICE.toString)) {
                    //                    val signed:Array[Char] = new Array[Char](1024)
                    //                    session.bs.read(signed)
                    signedBobMSG = new Message(session.bs.readLine()).getBytes
                    var publicKeyMSG = new Message(message.substring(message.lastIndexOf("}") + 1))
                    networkManager.addMessage("get signPublicKeyBob from " + sessionName)
                    if (!EDSrsa.verification(publicKeyMSG.getBytes, signedBobMSG, keysRSA.privateKey, publicKeyTrent))
                      networkManager.addMessage("Error, sign not validate!!!")
                    else
                      networkManager.addMessage("Verification, OK!")
                    publicKeyBob = parseKey(publicKeyMSG.getText).asInstanceOf[PublicKey]
                    networkManager.addMessage(publicKeyBob.toString)
                  }
                  else if (message.contains("{" + Commands.signPublicKeyTrent.toString + "}") && client.id.equals(OrderSessions.ALICE.toString)) {
                    //                    val signed:Array[Char] = new Array[Char](1024)
                    //                    session.bs.read(signed)
                    signedTrentMSG = new Message(session.bs.readLine()).getBytes
                    var publicKeyMSG = new Message(message.substring(message.lastIndexOf("}") + 1))
                    networkManager.addMessage("get signPublicKeyTrent from " + sessionName)
                    if (!EDSrsa.verification(publicKeyMSG.getBytes, signedTrentMSG, keysRSA.privateKey, publicKeyTrent))
                      networkManager.addMessage("Error, sign not validate!!!")
                    else
                      networkManager.addMessage("Verification, OK!")
                    publicKeyTrent = parseKey(publicKeyMSG.getText).asInstanceOf[PublicKey]
                    networkManager.addMessage(publicKeyTrent.toString)

                    /////
                    networkManager.addMessage("generate session key ")
                    sessionKey = SessionKey(random(1, 99))
                    networkManager.addMessage("sessionkey " + sessionKey.toString)

                    networkManager.addMessage("generate mark Ta ")
                    val markTa = new Date().getTime
                    networkManager.addMessage(new Date().toString)
                    val signedK = new Message(EDSrsa.sign(Array(sessionKey.key.toByte), keysRSA.privateKey, publicKeyBob))
                    val signedKb = new Message(EDSrsa.sign(new Message(publicKeyBob.toString).getBytes, keysRSA.privateKey, publicKeyBob))
                    val signedKa = new Message(EDSrsa.sign(new Message(keysRSA.publicKey.toString).getBytes, keysRSA.privateKey, publicKeyBob))
                    val msg = new Message(Action(client.id, OrderSessions.BOB.toString, List(Commands.sessionKey.toString)).toString + "sk:" + sessionKey.key + "ta:" + markTa + "ka:" + keysRSA.publicKey.toString + "kb:" + publicKeyBob.toString)
                    networkManager.sessions(0).ps.println(msg)
                    networkManager.addMessage("send E(S(K, Ta)), S(B, Kb), S(A, Ka) to " + OrderSessions.BOB.toString)
                    networkManager.sessions(0).ps.println(Action(client.id, OrderSessions.BOB.toString, List(Commands.sendBytes.toString)).toString + signedK.getChars)
                    networkManager.sessions(0).ps.println(Action(client.id, OrderSessions.BOB.toString, List(Commands.sendBytes.toString)).toString + signedKb.getChars)
                    networkManager.sessions(0).ps.println(Action(client.id, OrderSessions.BOB.toString, List(Commands.sendBytes.toString)).toString + signedKa.getChars)

                    networkManager.getUser.access = true
                    networkManager.addMessage("Access, true!")
                  }
                }
                else
                  networkManager.addMessage(message.substring(message.indexOf("]")+1), true)
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
      var publicKeyAlice: PublicKey = null
      var publicKeyBob: PublicKey = null
      var keysRSA: KeysRSA = null
      actor {
        while (true) {
          if (networkManager.sessions.nonEmpty)
            for (source <- networkManager.sessions) {

              try {
                if (source.bs.ready) {
                  val message = source.bs.readLine()
                  val sourceName = source.idSession //change
                  //0 - bob, 1 - alice, x - trent
                  //[] - who destination? <> - who source
                  if (message.contains("[" + OrderSessions.TRENT + "]")) {
                    if (message.contains("{" + Commands.publicKey.toString + "}")) {
                      val getPublicKey = parseKey(message).asInstanceOf[PublicKey]
                      networkManager.addMessage("get open key from " + sourceName)

                      networkManager.addMessage("send my open key to " + sourceName)
                      if (sourceName.equalsIgnoreCase(OrderSessions.ALICE.toString)) {
                        publicKeyAlice = getPublicKey


                      } else {
                        publicKeyBob = getPublicKey

                        networkManager.addMessage("generateKeys")
                        keysRSA = RSA.generateKeys(ConfRSA())
                        networkManager.addMessage(keysRSA.toString)

                        val msg = new Message(Action(OrderSessions.TRENT.toString, OrderSessions.BOB.toString, List(Commands.publicKey.toString)) + keysRSA.publicKey.toString)
                        networkManager.sessions(1).ps.println(msg)


                        val msg2 = new Message(Action(OrderSessions.TRENT.toString, OrderSessions.ALICE.toString, List(Commands.publicKey.toString)) + keysRSA.publicKey.toString)
                        networkManager.sessions(0).ps.println(msg2)
                      }
                    }
                    else if (message.contains("{" + Commands.AB.toString + "}")) {
                      networkManager.addMessage("get A, B from " + sourceName)

                      val msgKb = new Message(publicKeyBob.toString)
                      val signKb = new Message(EDSrsa.sign(msgKb.getBytes, keysRSA.privateKey, publicKeyAlice))
                      val msgB = new Message(Action(OrderSessions.TRENT.toString, OrderSessions.ALICE.toString, List(Commands.signPublicKeyBob.toString)).toString + publicKeyBob)
                      networkManager.addMessage("send S(B, Kb) to " + sourceName)
                      networkManager.sessions(0).ps.println(msgB)
                      networkManager.sessions(0).ps.println(signKb.getChars)

                      val msgKt = new Message(keysRSA.publicKey.toString)
                      val signKt = new Message(EDSrsa.sign(msgKt.getBytes, keysRSA.privateKey, publicKeyAlice))
                      val msgT = new Message(Action(OrderSessions.TRENT.toString, OrderSessions.ALICE.toString, List(Commands.signPublicKeyTrent.toString)).toString + keysRSA.publicKey)
                      networkManager.addMessage("send S(A, Kt) to " + sourceName)
                      networkManager.sessions(0).ps.println(msgT)
                      networkManager.sessions(0).ps.println(signKb.getChars)
                      mainFrame.dispose()
                    }
                  }
                  else if (sourceName.contains(OrderSessions.BOB.toString)) {
                    //                  if (message.contains("[" + OrderSessions.ALICE.toString + "]")) {
                    //                    if (message.contains(Action(OrderSessions.BOB.toString, OrderSessions.ALICE.toString, List(Commands.sendBytes.toString)).toString))
                    //                      networkManager.sessions(0).ps.println(message.substring(message.lastIndexOf("}") + 1))
                    //                    else
                    networkManager.sessions(0).ps.println(message)
                  }
                  else if (sourceName.contains(OrderSessions.ALICE.toString)) {
                    //                    else if (message.contains("[" + OrderSessions.BOB.toString + "]")) {
                    //                    if (message.contains(Action(OrderSessions.ALICE.toString, OrderSessions.BOB.toString, List(Commands.sendBytes.toString)).toString))
                    //                      networkManager.sessions(1).ps.println(message.substring(message.lastIndexOf("}") + 1))
                    //                    else
                    networkManager.sessions(1).ps.println(message)
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