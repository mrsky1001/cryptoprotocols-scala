package crypto.algorithms.gamma

import crypto.algorithms.extra.SessionKey

object Gamma {
  def encrypt(msg: Array[Byte], sessionKey: SessionKey): Array[Byte] = {
    msg.map(symbol => {
      (symbol ^ sessionKey.key).toByte
    })
  }

  def decrypt(msg: Array[Byte], sessionKey: SessionKey): Array[Byte] = {
    msg.map(symbol => {
      (symbol ^ sessionKey.key).toByte
    })
  }
}
