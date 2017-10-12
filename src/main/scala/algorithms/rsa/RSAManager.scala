package algorithms.rsa

import java.math.BigInteger
import java.security.SecureRandom
import javax.swing.JTextPane

import protocols.denningsacco.DenningSacco._

class RSAManager {

  val sizePQ: Int = 5
  var rsa: RSAJava = new RSAJava(sizePQ)

  var secretKey = 0
  var openKey = 0
  var sessionKey = 0

  def generateSecretKey(): Unit = {
    secretKey = rsa.getD.intValue()
  }

  def generateOpenKey: String = {
    openKey = rsa.getN.intValue
    rsa.getE.toString + "n:" + rsa.getN.toString + "p:" + rsa.getP.toString + "q:" + rsa.getQ
  }

  def generateSessionKey(): Unit = {
    sessionKey = new SecureRandom().nextInt(openKey - 10) + 1
  }

  def setOpenKey(message: String, textPane: JTextPane): Unit = {
    val indexN = message.indexOf("n:")
    val indexP = message.indexOf("p:")
    val indexQ = message.indexOf("q:")

    val e = new BigInteger(message.substring(0, indexN))
    val n = new BigInteger(message.substring(indexN + 2, indexP))
    val p = new BigInteger(message.substring(indexP + 2, indexQ))
    val q = new BigInteger(message.substring(indexQ + 2))

    System.out.println("e, n, p ,q ")
    System.out.println(e + " " + n + " " + p + " " + q)
    rsa = new RSAJava(sizePQ, e, n, p, q, textPane)
    openKey = n.intValue
  }

  def encrypt(key: Int): String = rsa.encrypt(key)

  def decrypt(str: String, jTextPane: JTextPane): Int = rsa.decrypt(new BigInteger(str), messagesPane)
}
