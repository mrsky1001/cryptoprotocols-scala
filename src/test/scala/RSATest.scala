import crypto.algorithms.extra.ExtraFunc
import crypto.algorithms.rsa.{ConfRSA, KeysRSA, RSA}
import org.scalatest.FunSpec

class RSATest extends FunSpec with ExtraFunc {
  describe("=> check RSA: encrypt-decrypt ") {
    val msg = "123-hello-pxz"
    val keysRSA: KeysRSA = RSA.generateKeys(ConfRSA())
    println(keysRSA)

    val C = RSA.encrypt(parseToArray(msg), keysRSA.publicKey)
    println(parseToString(C))

    val M = parseToString(RSA.decrypt(C, keysRSA.privateKey))
    println(M)

    assert(msg.equals(M))
  }

}
