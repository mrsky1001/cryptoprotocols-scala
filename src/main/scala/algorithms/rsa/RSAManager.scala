package algorithms.rsa

import java.math.BigInteger
import java.security.SecureRandom

import entity.{OpenKey, SecretKey}

class RSAManager {

  val sizePQ: Int = 5
  var rsa: RSAJava = new RSAJava(sizePQ)

  def getSecretKey: SecretKey = {
    SecretKey(rsa.getD, rsa.getN)
  }

  def getOpenKey: OpenKey = {
    OpenKey(rsa.getE, rsa.getN, rsa.getP, rsa.getQ)
  }


  def generateSessionKey(openKey: OpenKey): Int = {
    new SecureRandom().nextInt(Math.abs(openKey.e.intValue() - 10)) + 1
  }

  def parseOpenKey(message: String): OpenKey = {
    val indexE = message.indexOf("e:")
    val indexN = message.indexOf("n:")
    val indexP = message.indexOf("p:")
    val indexQ = message.indexOf("q:")

    val e = new BigInteger(message.substring(indexE + 2, indexN))
    val n = new BigInteger(message.substring(indexN + 2, indexP))
    val p = new BigInteger(message.substring(indexP + 2, indexQ))
    val q = new BigInteger(message.substring(indexQ + 2))

    System.out.println("e, n, p ,q ")
    System.out.println(e + " " + n + " " + p + " " + q)
    OpenKey(e, n, p, q)
  }

  def setOpenKey(openKey: OpenKey): Unit = {
    val e = new BigInteger(openKey.e.toString)
    val n = new BigInteger(openKey.n.toString)
    val p = new BigInteger(openKey.p.toString)
    val q = new BigInteger(openKey.q.toString)

    System.out.println("e, n, p ,q ")
    System.out.println(e + " " + n + " " + p + " " + q)
    rsa = new RSAJava(sizePQ, e, n, p, q)
  }

  def encrypt(key: BigInteger): BigInteger = rsa.encrypt(key)

  def decrypt(message: String): BigInteger = rsa.decrypt(new BigInteger(message))
}
