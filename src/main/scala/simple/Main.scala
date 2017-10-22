package simple

import java.math.BigInteger
import java.security.spec._
import java.security.{KeyFactory, Security}
import javax.crypto.Cipher
//  import crypto._
//  import protocol.defaults._

object Main extends App {

  Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())

  val input = "hel".getBytes()


  val cipher = Cipher.getInstance("RC4", "BC")

  val keyFactory = KeyFactory.getInstance("rsa", "BC")
  val pubKeySpec = new RSAPublicKeySpec(new BigInteger(
    "12345678", 16), new BigInteger("11", 16))
  val privKeySpec = new RSAPrivateKeySpec(new BigInteger(
    "12345678", 16), new BigInteger("12345678",
    16))

  val pubKey = keyFactory.generatePublic(pubKeySpec)
  val privKey = keyFactory.generatePrivate(privKeySpec)

  cipher.init(Cipher.ENCRYPT_MODE, pubKey)

  val cipherText = cipher.doFinal(input)
  System.out.println("cipher: " + new String(cipherText))

  cipher.init(Cipher.DECRYPT_MODE, privKey)
  val plainText = cipher.doFinal(cipherText)
  System.out.println("plain : " + new String(plainText))

  //
//  import java.security.KeyPairGenerator
//
//  var publicKey = PublicKey
//  var privateKey = PrivateKey
//  try {
//    val kpg = KeyPairGenerator.getInstance("RSA")
//    kpg.initialize(1024)
//    val kp = kpg.genKeyPair
//    publicKey = kp.getPublic
//    privateKey = kp.getPrivate
//  } catch {
//    case e: Exception =>
//      println("Crypto", "RSA key pair error")
//  }
//
//  // Encode the original data with RSA private key
//  var encodedBytes = null
//  try {
//    val c = Cipher.getInstance("RSA")
//    c.init(Cipher.ENCRYPT_MODE, privateKey)
//    val msg = "hello"
//    encodedBytes = c.doFinal(msg.toByte)
//  } catch {
//    case e: Exception =>
//      println("Crypto", "RSA encryption error")
//  }
//  println("[ENCODED]:\n" + Base64.getEncoder.encodeToString(encodedBytes) + "\n")
//
//  // Decode the encoded data with RSA public key
//  var decodedBytes = null
//  try {
//    val c = Cipher.getInstance("RSA")
//    c.init(Cipher.DECRYPT_MODE, publicKey)
//    decodedBytes = c.doFinal(encodedBytes)
//  } catch {
//    case e: Exception =>
//      Log.e("Crypto", "RSA decryption error")
//  }
 
//  def encodeBase64(bytes: Array[Byte]) = Base64.encodeBase64(bytes)
////    println(encodeBase64(RSA.encrypt("hello", "123")))
////    println(encodeBase64(RSA.encrypt("hello", "123")))
//  println(encodeBase64(RSA.encrypt("hoge".getBytes(), "01234567")))
//  //=> vyudTtnBJfs=
//
//  println(encodeBase64(AES.encrypt("hoge", "0123456789012345")))
//  //=> QWSouZUMVYMfS86xFyBgtQ==
//
//  println(encodeBase64(DES.encrypt(123L, "01234567")))
//  //=> Cqw2ipxTtvIIu122s3wG1w==
//
//  println(encodeBase64(DES.encrypt(123, "01234567")))
  //=> BV+LSCSYmUU=
}