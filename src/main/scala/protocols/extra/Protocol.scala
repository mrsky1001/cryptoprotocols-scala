package protocols.extra

import crypto.algorithms.extra.{CryptoKey, PublicKey, SessionKey}

trait Protocol {

  def parseKey(message: String): CryptoKey = {
    val indexKey = message.indexOf("key:")
    val indexN = message.indexOf("n:")
    val indexKB = message.indexOf("kb:")

    val key = message.substring(indexKey + 4, indexN)
    if(indexKB != -1)
      PublicKey(key.toInt,  message.substring(indexN + 2, indexKB).toInt)
    else
      PublicKey(key.toInt,  message.substring(indexN + 2).toInt)

  }

}
