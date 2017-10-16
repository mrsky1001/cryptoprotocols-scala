package protocols.denningsacco

import algorithms.rsa.RSAManager
import entity.{OpenKey, SecretKey}

class Participant(rsaManager: RSAManager) {
  def getRSAManager: RSAManager = rsaManager

  def getOpenKey: OpenKey = rsaManager.getOpenKey

  def getSecretKey: SecretKey = rsaManager.getSecretKey

  def generateSessionKey(openKey: OpenKey): Int = rsaManager.generateSessionKey(openKey)

  def parseOpenKey(openKey: String): OpenKey = {
    rsaManager.parseOpenKey(openKey)
  }
}