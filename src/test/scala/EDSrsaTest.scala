import crypto.algorithms.extra.{ExtraFunc, Message}
import crypto.algorithms.eds.EDSrsa
import crypto.algorithms.rsa.{ConfRSA, KeysRSA, RSA}
import org.scalatest.FunSpec

class EDSrsaTest extends FunSpec with ExtraFunc {
  describe("=> check EDSonRSA: sign-verification ") {
    val msg = new Message("key:107n:203")
    val keysRSASource: KeysRSA = RSA.generateKeys(ConfRSA())
    val keysRSADestination: KeysRSA = RSA.generateKeys(ConfRSA())
    println(keysRSASource)
    println(keysRSADestination)

    val encSignedMSG = new Message(EDSrsa.sign(msg.getBytes, keysRSASource.privateKey, keysRSADestination.publicKey))
    println(encSignedMSG)
    val strReplace = encSignedMSG.getTextReplaced
    val strUnReplace = encSignedMSG.getTextUnReplaced

    assert(EDSrsa.verification(msg.getBytes, strUnReplace.getBytes, keysRSADestination.privateKey, keysRSASource.publicKey))
  }
}
