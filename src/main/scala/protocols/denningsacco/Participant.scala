//package protocols.denningsacco
//
//import RSA.RSAManager
//import entity.{PublicKey, SecretKey}
//
//class Participant(rsaManager: RSAManager) {
//  def getRSAManager: RSAManager = rsaManager
//
//  def getOpenKey: PublicKey = rsaManager.getOpenKey
//
//  def getSecretKey: SecretKey = rsaManager.getSecretKey
//
//  def generateSessionKey(openKey: PublicKey): Int = rsaManager.generateSessionKey(openKey)
//
//  def parseOpenKey(openKey: String): PublicKey = {
//    rsaManager.parseOpenKey(openKey)
//  }
//}