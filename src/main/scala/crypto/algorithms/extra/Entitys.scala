package crypto.algorithms.extra

trait CryptoKey {
  def key: Int

  def n: Int
}

case class PublicKey(key: Int, n: Int = 0) extends CryptoKey

case class PrivateKey(key: Int, n: Int = 0) extends CryptoKey

case class SessionKey(key: Int)

class Message(text: String, bytes: Array[Byte]) {
  def this(text: String) = this(text, text.map(c => c.toByte).toArray)

  def this(bytes: Array[Byte]) = this(String.valueOf(bytes.map(c => if (c < 0) (128 * 2 + c).toChar else c.toChar)), bytes)

  def getText: String = text

  def getBytes: Array[Byte] = bytes

  override def toString = text

  override def equals(obj: scala.Any): Boolean = {
    if (getClass != obj.getClass)
      return false

    val message = obj.asInstanceOf[Message]
    if (!text.equals(message.getText))
      return false

    bytes.sameElements(message.getBytes)
  }
}
