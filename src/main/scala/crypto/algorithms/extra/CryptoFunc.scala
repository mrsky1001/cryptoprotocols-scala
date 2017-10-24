package crypto.algorithms.extra

import crypto.algorithms.rsa.ConfRSA

import scala.annotation.tailrec

trait CryptoFunc extends ExtraFunc {
  val isPrime = (p: Int) => (factorize(p).sum + 1).equals(p + 1)

  def encrypt(msg: Array[Byte], cryptoKey: CryptoKey): Array[Byte]

  def decrypt(encryptedMSG: Array[Byte], cryptoKey: CryptoKey): Array[Byte]

  def inverse(a: Int, m: Int): Int = {
    def modInv(a: Int, m: Int, x: Int = 1, y: Int = 0): Int = if (m == 0) x else modInv(m, a % m, y, x - y * (a / m))

    val res = modInv(a, m)
    if (res < 0)
      m + res
    else
      res
  }

  def generatePrime(min: Int, max: Int, p: Int = 0): Int = {
    var a = random(min, max)
    if(min == 1 && max == 1)
      return 1
    while (a < 2 || a % 2 == 0 || a == p || !isPrime(a))
      a = random(min, max)
    a
  }

  def powMod(x: Array[Byte], y: Int, mod: Int): Array[Byte] = {
    x.map { byte =>
      var b = byte.toInt
      while (b < 0)
        b = 128 * 2 + b

      var z = b
      var n = y - 1
      while (n > 0) {
        z = (z * b) % mod
        n -= 1
      }
      z.toByte
    }
  }

  def isHaveInverseElement(e: Int, mod: Int): Boolean = {
    val d = inverse(e, mod)
    (e * d % mod).equals(1)
  }

  def factorize(x: Int): List[Int] = {
    @tailrec
    def foo(x: Int, a: Int = 2, list: List[Int] = Nil): List[Int] = a * a > x match {
      case false if x % a == 0 => foo(x / a, a, a :: list)
      case false => foo(x, a + 1, list)
      case true => x :: list
    }

    foo(x)
  }
}
