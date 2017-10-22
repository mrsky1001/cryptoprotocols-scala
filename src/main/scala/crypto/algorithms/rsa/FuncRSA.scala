package crypto.algorithms.rsa

trait FuncRSA {
  def phi(parameters: Parameters): Int = (parameters.p - 1) * (parameters.q - 1)
}