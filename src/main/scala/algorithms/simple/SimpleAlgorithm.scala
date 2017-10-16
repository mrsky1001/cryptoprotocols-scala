package algorithms.simple

  import javax.crypto.spec.SecretKeySpec
  import javax.crypto.Cipher

  import protocols.simple.Writes

  trait Encryption {
    def encrypt(dataBytes: Array[Byte], secret: String): Array[Byte]
    def decrypt(codeBytes: Array[Byte], secret: String): Array[Byte]

    def encrypt[T:Writes](data: T, secret: String): Array[Byte] = encrypt(implicitly[Writes[T]].writes(data), secret)
  }

  class JavaCryptoEncryption(algorithmName: String) extends Encryption {

    def encrypt(bytes: Array[Byte], secret: String): Array[Byte] = {
      val secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
      val encipher = Cipher.getInstance(algorithmName + "/ECB/PKCS5Padding")
      encipher.init(Cipher.ENCRYPT_MODE, secretKey)
      encipher.doFinal(bytes)
    }

    def decrypt(bytes: Array[Byte], secret: String): Array[Byte] = {
      val secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), algorithmName)
      val encipher = Cipher.getInstance(algorithmName + "/ECB/PKCS5Padding")
      encipher.init(Cipher.DECRYPT_MODE, secretKey)
      encipher.doFinal(bytes)
    }
  }

  object DES extends JavaCryptoEncryption("DES")
  object AES extends JavaCryptoEncryption("AES")
