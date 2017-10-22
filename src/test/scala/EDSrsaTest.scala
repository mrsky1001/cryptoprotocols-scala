import crypto.algorithms.extra.{ExtraFunc, Message}
import crypto.algorithms.hash.EDSrsa
import crypto.algorithms.rsa.{ConfRSA, KeysRSA, RSA}
import org.scalatest.FunSpec

class EDSrsaTest extends FunSpec with ExtraFunc {
  describe("=> check EDSonRSA: sign-verification ") {
    val msg = new Message("123-hello-pxz")
    val keysRSASource: KeysRSA = RSA.generateKeys(ConfRSA())
    val keysRSADestination: KeysRSA = RSA.generateKeys(ConfRSA())
    println(keysRSASource)
    println(keysRSADestination)

    val encSignedMSG = new Message(EDSrsa.sign(msg.getBytes, keysRSASource.privateKey, keysRSADestination.publicKey))
    println(encSignedMSG)

    assert(EDSrsa.verification(msg.getBytes, encSignedMSG.getBytes, keysRSADestination.privateKey, keysRSASource.publicKey))
  }
}
