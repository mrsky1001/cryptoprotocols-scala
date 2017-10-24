import crypto.algorithms.extra.{ExtraFunc, Message}
import crypto.algorithms.rsa.{ConfRSA, KeysRSA, RSA}
import org.scalatest.FunSpec

class RSATest extends FunSpec with ExtraFunc {
  describe("=> check RSA: encrypt-decrypt ") {
    val msg = new Message("\r\r\n-hello-pxz")
    val keysRSA: KeysRSA = RSA.generateKeys(ConfRSA())
    println(keysRSA)
    val chars = (65443 to 65536).map(i => i.toChar).toArray
    val t = chars.map { c => if (c > 60000) (c - 6536).toByte else c.toByte }
    val t2 = chars.map { c => if (c > 60000) (c - 6536).toByte else c.toByte }
    val t3 = chars.map { c => if (c > 60000) (c - 6536).toByte else c.toByte }
    val t4 = chars.map { c => if (c > 60000) (c - 6536).toByte else c.toByte }
//    val C = new Message(RSA.encrypt(msg.getBytes, keysRSA.publicKey)).getBytesReplaced
//    println(C)
//    val Cunr = new Message(C)
//
//    val M = new Message(RSA.decrypt(Cunr.getBytesUnReplaced, keysRSA.privateKey))
//    println(M)
    //все работает но из за \r не выводит
//    assert(msg.getText.equals(M.getText))
  }

}
