package crypto.algorithms.extra

import java.security.SecureRandom

trait ExtraFunc {
  val random = (min: Int, max: Int) => SecureRandom.getInstance("SHA1PRNG").nextInt((max - min) + 1) + min
  val parseToArray = (msg: String) => msg.map(c => c.toByte).toArray
  val parseToString = (msg: Array[Byte]) => String.valueOf(msg.map(c => if (c < 0) (128 * 2 + c).toChar else c.toChar))
}