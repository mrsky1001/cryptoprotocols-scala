import crypto.algorithms.extra.{ExtraFunc, Message}
import crypto.algorithms.rsa.{ConfRSA, KeysRSA, RSA}
import org.scalatest.FunSpec

class RSATest extends FunSpec with ExtraFunc {
  describe("=> check RSA: encrypt-decrypt ") {
    val msg = new Message("123-hello-pxz")
    val keysRSA: KeysRSA = RSA.generateKeys(ConfRSA())
    println(keysRSA)

    val C = new Message(RSA.encrypt(msg.getBytes, keysRSA.publicKey))
    println(C)

    val M = new Message(RSA.decrypt(C.getBytes, keysRSA.privateKey))
    println(M)

    assert(msg.getText.equals(M.getText))
  }

}
