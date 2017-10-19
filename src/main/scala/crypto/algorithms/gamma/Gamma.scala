package crypto.algorithms.gamma

object Gamma {
  private val A = 3
  private val C = 2
  private val m = 101

  def encrypt(text: String, key: Int): Array[Byte] = {
    var actualKey = key
    val arr = text.getBytes
    arr.map(symbol => {
      //checking
      actualKey = (actualKey * A + C) % m
      (symbol ^ actualKey).toByte
    })
  }

  def decrypt(text: Array[Byte], key: Int): String = {
    var actualKey = key
    text.map(byte => {
      //checking
      actualKey = (actualKey * A + C) % m
      (byte ^ actualKey).toByte
    }).toString
  }
}
