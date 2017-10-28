package crypto.algorithms.gamma

object Gamma {
  def encrypt(msg: Array[Byte], sessionKey: Int): Array[Byte] = {
    msg.map(symbol => {
      (symbol ^ sessionKey).toByte
    })
  }

  def decrypt(msg: Array[Byte], sessionKey: Int): Array[Byte] = {
    msg.map(symbol => {
      (symbol ^ sessionKey).toByte
    })
  }
}
