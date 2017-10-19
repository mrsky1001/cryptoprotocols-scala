//package algorithms
//
//import java.math.BigInteger
//
//import RSA.RSAManager
//import entity.OpenKey
//import protocols.denningsacco.DenningSacco.participant
//import protocols.denningsacco.Participant
//
//object CRC {
//  val N = 947
//
//  def sign(openKey: OpenKey, openKeyDestination: OpenKey, key: BigInteger): BigInteger = {
//    participant.getRSAManager().setOpenKey(openKey)
//    val signedKey = participant.getRSAManager().encrypt(key)
//
//    participant.getRSAManager().setOpenKey(openKeyDestination)
//    participant.getRSAManager().encrypt(signedKey)
//  }
//
//  def unsign(openKey: OpenKey, openKeySource: OpenKey, message: String): BigInteger = {
//    participant.getRSAManager().setOpenKey(openKey)
//    val signedKey = participant.getRSAManager().decrypt(message)
//
//    participant.getRSAManager().setOpenKey(openKeySource)
//    participant.getRSAManager().decrypt(signedKey.toString())
//  }
//}
//
//
////package algorithms
////
////object CRC {
////
////  def calcCRC(inp: String): String = {
////    val polynomial = 101
////    println("<----------------" + Integer.parseInt(inp, 2) + "----------------------->")
////    println("inp = " + inp)
////    println("polynomial= " + polynomial)
////
////    val N = polynomial.length
////    var inpM = inp
////    (0 until N).foreach { _ =>
////      inpM += "0"
////    }
////
////    var interimI = 0
////    var interimS = ""
////
////    var startIndex = 0
////    var endlIndex = polynomial.length
////    val first = inpM.substring(startIndex, endlIndex)
////    startIndex = endlIndex
////    interimI = Integer.parseInt(first, 2) ^ Integer.parseInt(polynomial, 2)
////    interimS = Integer.toBinaryString(interimI)
////
////    if (interimS.length < polynomial.length) {
////      endlIndex += polynomial.length - interimS.length
////      if (endlIndex >= inpM.length) {
////        endlIndex = inpM.length
////        interimS += inpM.substring(startIndex, endlIndex)
////        interimI = Integer.parseInt(interimS, 2)
////        println("CRC = " + interimS)
////        println("CRCI = " + interimI)
////        println("<------------------------------------------>")
////        println()
////        return interimS
////      }
////      interimS += inpM.substring(startIndex, endlIndex)
////      interimI = Integer.parseInt(interimS, 2)
////      startIndex = endlIndex
////    }
////
////    while (interimS.length > polynomial.length - 1) {
////      interimI = interimI ^ Integer.parseInt(polynomial, 2)
////      interimS = Integer.toBinaryString(interimI)
////
////      if (interimS.length < polynomial.length) {
////        endlIndex += polynomial.length - interimS.length
////        if (endlIndex > inpM.length) {
////          endlIndex = inpM.length
////          interimS += inpM.substring(startIndex, endlIndex)
////          interimI = Integer.parseInt(interimS, 2)
////          println("CRC = " + interimS)
////          println("CRCI = " + interimI)
////          println("<------------------------------------------>")
////          println()
////          return interimS
////        }
////        interimS += inpM.substring(startIndex, endlIndex)
////        interimI = Integer.parseInt(interimS, 2)
////        startIndex = endlIndex
////      }
////    }
////    interimS
////  }
////  def calcHEX(message: String, p: Int): Int = {
////    var file: String = new String
////    val CRC: String = L5.calcCRC(message, json)
////    val result = Integer.parseInt(CRC, 2) % p
////    if (result < 2) {
////      2
////    }
////    else if (result >= p) {
////      p - 1
////    }
////    else {
////      result
////    }
////
////  }
////
////  def DigitalSignature(message: String) {
////    val p = 101
////    val g = 29
////    val x = 3
////    val h = calcHEX(message, p)
////    val y = powMod(g, x, p)
////    val k = randomScala(p - 1, 2, p) % (p - 1)
////    val r = powMod(g, k, p)
////    val u = if ((h - x * r) % (p - 1) < 0) (p - 1) + (h - x * r) % (p - 1) else (h - x * r) % (p - 1)
////    val s = if ((inverse(k, p - 1) * u) % (p - 1) < 0) (p - 1) + (inverse(k, p - 1) * u) % (p - 1) else (inverse(k, p - 1) * u) % (p - 1)
////    println("(M, r, s): ")
////    println("M =  " + message)
////    println("r =  " + r)
////    println("s =  " + s)
////    println("k =  " + k)
////    println("inverse(k) =  " + inverse(k, p - 1))
////
////    val yrrs = (powMod(y, r, p) * powMod(r, s, p)) % p
////    val gh = powMod(g, h, p)
////    println("y^r * r^s = " + yrrs)
////    println("g^h mod p =  " + gh)
////  }
////}
