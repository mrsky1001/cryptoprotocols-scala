package crypto.algorithms.rsa

import crypto.algorithms.extra.{PrivateKey, PublicKey}

case class ConfRSA(val keySize: Int = 256)

case class Parameters(p: Int, q: Int)

case class KeysRSA(publicKey: PublicKey, privateKey: PrivateKey)
