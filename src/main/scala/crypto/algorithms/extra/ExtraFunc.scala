package crypto.algorithms.extra

import java.security.SecureRandom

trait ExtraFunc {
  val random = (min: Int, max: Int) => SecureRandom.getInstance("SHA1PRNG").nextInt((max - min) + 1) + min
}