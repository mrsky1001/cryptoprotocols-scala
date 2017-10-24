package crypto.algorithms.rsa

import crypto.algorithms.extra.{CryptoFunc, CryptoKey, PrivateKey, PublicKey}

object RSA extends CryptoFunc with FuncRSA {

  def generateKeys(conf: ConfRSA): KeysRSA = {
    val size = conf.keySize-56
    val p = generatePrime(3, Math.sqrt(size).toInt)
    val q = generatePrime(128/p, size/p, p)
    val parameters = Parameters(p, q)
    println(parameters)
    val n = parameters.p * parameters.q

    val publicKey = generatePublicKey(n)
    val privateKey = generatePrivateKey(publicKey)

    KeysRSA(publicKey, privateKey)
  }

  def generatePublicKey(n: Int): PublicKey = {
    val parameters = getParameters(n)
    val phiLocal = phi(parameters)
    var e = random(3, phiLocal)

    while (e == 1 || e % 2 == 0 || !isHaveInverseElement(e, phiLocal) || e == inverse(e, phiLocal)) {
      e = random(3, phiLocal)
    }
    PublicKey(e, n)
  }

  def generatePrivateKey(publicKey: PublicKey): PrivateKey = {
    val parameters = getParameters(publicKey.n)
    PrivateKey(inverse(publicKey.key, phi(parameters)), publicKey.n)
  }

  def getParameters(n: Int): Parameters = {
    val factorizePK = factorize(n)
    Parameters(factorizePK.head, factorizePK.last)
  }

  override def encrypt(msg: Array[Byte], cryptoKey: CryptoKey): Array[Byte] = {
    powMod(msg, cryptoKey.key, cryptoKey.n)
  }

  override def decrypt(encryptedMSG: Array[Byte], cryptoKey: CryptoKey): Array[Byte] = {
    powMod(encryptedMSG, cryptoKey.key, cryptoKey.n)
  }
}
