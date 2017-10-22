import crypto.algorithms.extra.ExtraFunc
import crypto.algorithms.hash.EDSonRSA
import crypto.algorithms.rsa.{ConfRSA, KeysRSA, RSA}
import org.scalatest.FunSpec

class EDSonRSATest extends FunSpec with ExtraFunc {
  describe("=> check EDSonRSA: sign-verification ") {
    val msg = "123-hello-pxz"
    val keysRSASource: KeysRSA = RSA.generateKeys(ConfRSA())
    val keysRSADestination: KeysRSA = RSA.generateKeys(ConfRSA())
    println(keysRSASource)
    println(keysRSADestination)

    val encSignedMSG = EDSonRSA.sign(parseToArray(msg), keysRSASource.privateKey, keysRSADestination.publicKey)
    println(parseToString(encSignedMSG))

    assert(EDSonRSA.verification(parseToArray(msg), encSignedMSG, keysRSADestination.privateKey, keysRSASource.publicKey))
  }
}
