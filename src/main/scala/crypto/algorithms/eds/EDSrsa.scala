package crypto.algorithms.eds

import crypto.algorithms.extra.{PrivateKey, PublicKey}
import crypto.algorithms.rsa.RSA

//Electronic digital signature
object EDSrsa {
  def sign(msg: Array[Byte], privateKey: PrivateKey, publicKeyDestination: PublicKey): Array[Byte] = {
    val signedMSG = RSA.encrypt(msg, privateKey)
    RSA.encrypt(signedMSG, publicKeyDestination)
  }

  def verification(msg: Array[Byte], signedMSG: Array[Byte], privateKey: PrivateKey, publicKeySource: PublicKey): Boolean = {
    val decryptMSG = RSA.decrypt(signedMSG, privateKey)
    val signed = RSA.decrypt(decryptMSG, publicKeySource)
    msg.sameElements(msg.take(msg.length))
  }


}

