package protocols.extra

import crypto.algorithms.extra.{CryptoKey, PublicKey, SessionKey}

trait Protocol {

  def parseKey(message: String): CryptoKey = {
    val indexKey = message.indexOf("key:")
    val indexN = message.indexOf("n:")

    val key = message.substring(indexKey + 4, indexN)
    val n = message.substring(indexN + 2)

//    if (message.contains("{publicKey}"))
      PublicKey(key.toInt, n.toInt)
//    else
//      null
  }

}
