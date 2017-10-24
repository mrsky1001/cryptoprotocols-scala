package crypto.algorithms.extra

import java.io.ByteArrayOutputStream
import java.security.SecureRandom

trait ExtraFunc {
  val random = (min: Int, max: Int) => SecureRandom.getInstance("SHA1PRNG").nextInt((max - min) + 1) + min

  def mergerByteArrays(first: Array[Byte], second: Array[Byte]): Array[Byte] = {
    val outStream = new ByteArrayOutputStream
    outStream.write(first)
    outStream.write(second)
    outStream.toByteArray
  }
}