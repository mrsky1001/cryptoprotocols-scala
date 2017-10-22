import crypto.algorithms.extra.{ExtraFunc, Message, SessionKey}
import crypto.algorithms.gamma.Gamma
import org.scalatest.FunSpec

class GammaTest extends FunSpec with ExtraFunc {
  describe("=> check EDSonRSA: sign-verification ") {
    val msg = new Message("123-hello-pxz")
    val sessionKey = SessionKey(77)
    println(sessionKey)

    val encMSG = new Message(Gamma.encrypt(msg.getBytes, sessionKey))
    println(encMSG)

    val decMSG = new Message(Gamma.decrypt(encMSG.getBytes, sessionKey))
    println(decMSG)

    assert(msg.equals(decMSG))
  }
}
