package crypto.algorithms.RSA

import scala.annotation.tailrec
import scala.util.Random

trait CryptoAlgorithm {
  def encrypt(text: String, publicKey: PublicKey): BigInt

  def decrypt(encryptedText: BigInt, privateKey: PrivateKey): String

  def inverse(a: BigInt, m: BigInt): BigInt = {
    def modInv(a: BigInt, m: BigInt, x: BigInt = 1, y: BigInt = 0): BigInt = if (m == 0) x else modInv(m, a % m, y, x - y * (a / m))

    val res = modInv(a, m)
    if (modInv(a, m) < 0)
      m + res
    else
      res
  }

  def generatePrime(bitLength: Int): BigInt = {
    BigInt.apply(bitLength, 1000, Random)
  }

  def powMod(p: BigInt, q: BigInt, n: BigInt): BigInt = {
    if (q == 0) BigInt(1)
    else if (q == 1) p
    else if (q % 2 == 1) powMod(p, q - 1, n) * p % n
    else powMod(p * p % n, q / 2, n)
  }

  def factorize(x: BigInt): List[BigInt] = {
    @tailrec
    def foo(x: BigInt, a: BigInt = 2, list: List[BigInt] = Nil): List[BigInt] = a * a > x match {
      case false if x % a == 0 => foo(x / a, a, a :: list)
      case false => foo(x, a + 1, list)
      case true => x :: list
    }

    foo(x)
  }

}

trait FuncRSA {
  def phi(parameters: Parameters): BigInt = (parameters.p - BigInt(1)) * (parameters.q - BigInt(1))

  def getBigIntText(text: String) = BigInt(text.map(x => "%03d".format(x.toInt)).reduceLeft(_ + _))

  def getStringBigInt(bigInt: String): String = {
    if (bigInt.length % 3 == 2)
      ("0" + bigInt).grouped(3).toList.foldLeft("")(_ + _.toInt.toChar)
    else
      bigInt.grouped(3).toList.foldLeft("")(_ + _.toInt.toChar)
  }
}

case class ConfRSA(val keySize: Int = 9)

case class Parameters(p: BigInt, q: BigInt)

case class PublicKey(e: BigInt, n: BigInt)

case class PrivateKey(d: BigInt, n: BigInt)

case class KeysRSA(publicKey: PublicKey, privateKey: PrivateKey)

class RSA(conf: ConfRSA) extends CryptoAlgorithm with FuncRSA {

  def generateKeys(): KeysRSA = {
    val parameters = Parameters(generatePrime(conf.keySize / 2), generatePrime(conf.keySize / 2))
    val n = parameters.p * parameters.q
    val publicKey = generatePublicKey(n)
    val privateKey = generatePrivateKey(publicKey)
    KeysRSA(publicKey, privateKey)
  }

  def generatePublicKey(n: BigInt): PublicKey = {
    val parameters = getParameters(n)

    var e = BigInt(Random.nextInt(phi(parameters).intValue() - 1))
    while (e % 2 == 0)
      e = BigInt(Random.nextInt(phi(parameters).intValue() - 1))

    while (!e.isProbablePrime(phi(parameters).intValue())) {
      e = e + BigInt(2)
    }
    PublicKey(e, n)
  }

  def generatePrivateKey(publicKey: PublicKey): PrivateKey = {
    val parameters = getParameters(publicKey.n)
    PrivateKey(inverse(publicKey.e, phi(parameters)), publicKey.n)
  }

  def getParameters(n: BigInt): Parameters = {
    val factorizePK = factorize(n)
    Parameters(factorizePK.head, factorizePK.last)
  }

  override def encrypt(text: String, publicKey: PublicKey): BigInt = {
    powMod(getBigIntText(text), publicKey.e, publicKey.n)
  }

  override def decrypt(encryptedText: BigInt, privateKey: PrivateKey): String = {
    val decBigInt = powMod(encryptedText, privateKey.d, privateKey.n)
    getStringBigInt(decBigInt.toString())
  }
}

object Main extends App {
  val rsa = new RSA(ConfRSA())
  val keysRSA: KeysRSA = rsa.generateKeys()
  val C = rsa.encrypt("hello", keysRSA.publicKey)
  println(C)
  val M = rsa.decrypt(C, keysRSA.privateKey)
  println(M)
}