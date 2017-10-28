package crypto.algorithms.rsa

import crypto.algorithms.extra.{CryptoFunc, CryptoKey, PrivateKey, PublicKey}

object RSA extends CryptoFunc with FuncRSA {

  def generateKeys(conf: ConfRSA): KeysRSA = {
//    val size = conf.keySize-56
//    val p = generatePrime(3, Math.sqrt(size).toInt)
//    val q = generatePrime(129/p, size/p, p)
//    val parameters = Parameters(p, q)
//    println(parameters)
//    val n = parameters.p * parameters.q

    val p = 113
    var c = generatePrime(3, p-1)
    while ((p-1) % c == 0 || !isHaveInverseElement(c, p-1)) {
      c = generatePrime(3, p-1)
    }
    val d = inverse(c, p-1)
    val parameters = Parameters(c, d)
    println(parameters)
    val publicKey = PublicKey(d, p)
    val privateKey = PrivateKey(c, p)

    KeysRSA(publicKey, privateKey)
  }
//
//  def generatePublicKey(n: Int): PublicKey = {
//    val parameters = getParameters(n)
//    val phiLocal = phi(parameters)
//    var e = random(3, phiLocal)
//
//    while (e == 1 || e % 2 == 0 || !isHaveInverseElement(e, phiLocal) || e == inverse(e, phiLocal)) {
//      e = random(3, phiLocal)
//    }
//    PublicKey(e, n)
//  }
//
//  def generatePrivateKey(publicKey: PublicKey): PrivateKey = {
//    val parameters = getParameters(publicKey.n)
//    PrivateKey(inverse(publicKey.key, phi(parameters)), publicKey.n)
//  }

//  def getParameters(n: Int): Parameters = {
//    val factorizePK = factorize(n)
//    Parameters(factorizePK.head, factorizePK.last)
//  }

//  override def encrypt(msg: Array[Byte], cryptoKey: CryptoKey): Array[Byte] = {
//    powMod(msg, cryptoKey.key, cryptoKey.n)
//  }
//
//  override def decrypt(encryptedMSG: Array[Byte], cryptoKey: CryptoKey): Array[Byte] = {
//    powMod(encryptedMSG, cryptoKey.key, cryptoKey.n)
//  }
override def encrypt(msg: Int, cryptoKey: CryptoKey): Int = {
  powMod(msg, cryptoKey.key, cryptoKey.n)
}

  override def decrypt(encryptedMSG: Int, cryptoKey: CryptoKey):Int = {
    powMod(encryptedMSG, cryptoKey.key, cryptoKey.n)
  }
}
