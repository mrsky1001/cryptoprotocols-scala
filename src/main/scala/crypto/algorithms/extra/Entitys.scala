package crypto.algorithms.extra

trait CryptoKey {
  def key: Int

  def n: Int
}

case class PublicKey(key: Int, n: Int = 0) extends CryptoKey

case class PrivateKey(key: Int, n: Int = 0) extends CryptoKey