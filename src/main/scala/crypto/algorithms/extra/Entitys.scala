package crypto.algorithms.extra

trait CryptoKey {
  def key: Int

  def n: Int

  override def toString: String = "key:" + key + "n:" + n
}

case class PublicKey(key: Int, n: Int = 0) extends CryptoKey

case class PrivateKey(key: Int, n: Int = 0) extends CryptoKey

case class SessionKey(key: Int)

class Message(text: String, bytes: Array[Byte]) {
  def this(text: String) = this(text, text.map(c => c.toByte).toArray)

  def this(bytes: Array[Byte]) = {
    this(String.valueOf(bytes.map(c => c.toChar)), bytes)
  }

  def this(chars: Array[Char]) = {
    this(chars.map { c => if (c > 60000) (c - 65536).toByte else c.toByte })
  }

  def getText: String = text.map(c => if (c > 60000) (c - 65536).toChar else c)

  def getTextReplaced: String = String.valueOf(text.map(c => (c + 32).toChar))

  def getTextUnReplaced: String = String.valueOf(text.map(c => (c - 32).toChar))

  def getBytes: Array[Byte] = bytes.map(b => if (b > 60000) (b - 65536).toByte else b)

  def getChars: Array[Char] = bytes.map(c => c.toChar)

  //  def getBytesUnReplaced: Array[Byte] = bytes.map(c => (c - 32).toByte)
  //  def getBytesReplaced: Array[Byte] = bytes.map(c => (c + 32).toByte)
  //
  //  def getBytesUnReplaced: Array[Byte] = bytes.map(c => (c - 32).toByte)

  override def toString: String = text

  override def equals(obj: scala.Any): Boolean = {
    if (getClass != obj.getClass)
      return false

    val message = obj.asInstanceOf[Message]
    if (!text.equals(message.getText))
      return false

    bytes.sameElements(message.getBytes)
  }
}
