package dao

import java.io._
import java.util
import java.nio.file.{Files, Paths}
import java.security.{SecureRandom, SecureRandomSpi}

import entity._
import org.json4s
import org.json4s.{DefaultFormats, JValue}

import scala.Serializable
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.io.Source

object Dao {

  def writeBytes(fileName: String, array: Array[Byte]): Unit = {
    val out = new FileOutputStream(new File(fileName))
    out.write(array)
    out.close()
  }

  def readBytes(file: File): Array[Byte] = {
    val is = new FileInputStream(file)
    val bytesInp = new Array[Byte](file.length.toInt)
    is.read(bytesInp)
    is.close()
    bytesInp
  }

  def readString(filename: String): String = {
    new String(Files.readAllBytes(Paths.get(filename)))
  }

  def writeString(filename: String, listBuffer: ListBuffer[Int]): Unit = {
    Some(new PrintWriter(filename)).foreach { p => listBuffer.iterator.foreach(l => p.write(l.toString)); p.close() }
  }

  def writeString(filename: String, str: String): Unit = {
    Some(new PrintWriter(filename)).foreach { p => p.write(str); p.close() }
  }

  def stringBitsToBitSet(array: String): util.BitSet = {
    val bits: util.BitSet = new util.BitSet()
    for (i <- 0 until array.length) {
      if (array(i) == '1') {
        bits.set( i)
      }
    }
    bits
  }

  def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

  def factorize(x: Int): List[Int] = {
    @tailrec
    def foo(x: Int, a: Int = 2, list: List[Int] = Nil): List[Int] = a * a > x match {
      case false if x % a == 0 => foo(x / a, a, a :: list)
      case false => foo(x, a + 1, list)
      case true => x :: list
    }
    foo(x)
  }

  def inverse2(aa: Int, n: Int): Int = {
    var a = aa
    var b = n
    var x = 0
    var d = 1
    while (a > 0 )
    {
      val q = b /a
      var y = a
      a = b % a
      b = y
      y = d
      d = x - (q * d)
      x = y
    }
    x = x % n
    if (x < 0)
    {
      x = (x + n) % n
    }
    x
  }

  ///val mapAlfavit = Map(1040 -> 10, 1041 -> 11, 1042 -> 12, 1043 -> 13, 1044 -> (14), 1045 -> (15), 1046 -> (16), 1047 -> (17), 1048 -> (18), 1049 -> (19), 1050 -> (20), 1051 -> (21), 1052 -> (22), 1053 -> (23), 1054 -> 24, 1055 -> (25), 1056 -> (26), 1057 -> (27), 1057 -> (28), 1058 -> (29), 1059 -> (30), 1060 -> (31), 1061 -> (32), 1062 -> (33), 1063 -> (34), 1064 -> (35), 1065 -> (36), 1066 -> (37), 1067 -> (38), 1068 -> (39), 1069 -> (40), 1070 -> (41))
  val mapAlfavit = Map('А' -> 42, 'Б' -> 11, 'В' -> 12, 'Г' -> 13, 'Д' -> 14, 'Е' -> 15, 'Ж' -> 16, 'З' -> 17, 'И' -> 18, 'Й' -> 19, 'К' -> 43, 'Л' -> 21, 'М' -> 22, 'Н' -> 23, 'О' -> 24, 'П' -> 25, 'Р' -> 26, 'С' -> 27, 'Т' -> 28, 'У' -> 29, 'Ф' -> 44, 'Х' -> 31, 'Ц' -> 32, 'Ч' -> 33, 'Ш' -> 34, 'Щ' -> 35, 'Ъ' -> 36, 'Ы' -> 37, 'Ь' -> 38, 'Э' -> 39, 'Ю' -> 45, 'Я' -> 41, ' ' -> 99)
  val mapAlfavitInt = Map(42 -> 'А', 11 -> 'Б', 12 -> 'В', 13 -> 'Г', 14 -> 'Д', 15 -> 'Е', 16 -> 'Ж', 17 -> 'З', 18 -> 'И', 19 -> 'Й', 43 -> 'К', 21 -> 'Л', 22 -> 'М', 23 -> 'Н', 24 -> 'О', 25 -> 'П', 26 -> 'Р', 27 -> 'С', 28 -> 'Т', 29 -> 'У', 44 -> 'Ф', 31 -> 'Х', 32 -> 'Ц', 33 -> 'Ч', 34 -> 'Ш', 35 -> 'Щ', 36 -> 'Ъ', 37 -> 'Ы', 38 -> 'Ь', 39 -> 'Э', 45 -> 'Ю', 41 -> 'Я', 99 -> ' ')

  def stringToNumber(str: String): ListBuffer[Int] = {
    val strNew = str.toUpperCase
    var listBloks = new ListBuffer[Int]
    strNew.iterator.foreach { j =>
      listBloks += mapAlfavit(j)
    }
    listBloks
  }

  def numbersToString(listBuf: ListBuffer[Int]): String = {
    var strN = new String
    var str = new String
    listBuf.iterator.foreach(j => strN += j.toString)
    for (i <- 0 until(strN.length - 1, 2)) {
      str += mapAlfavitInt(strN.substring(i, i + 2).toInt)
    }
    str
  }

  def powMod(x: Int, y: Int, mod: Int): Int = {
    var z = x
    var n = y - 1
    while (n > 0) {
      z = (z * x) % mod
      n -= 1
    }
    z
  }

  def ArrayByteToArrayByteR(arrayByte: Array[Byte]): Array[ByteR] = {
    arrayByte.map { byte => new ByteR(byte) }
  }
  def randomScala(max:Int, min:Int, p:Int): Int ={
    var rand = 1
    var srand = new scala.util.Random(System.nanoTime)
    while((inverse(rand, p - 1)*rand%(p-1)) != 1 || rand == 1){
      srand = new scala.util.Random(System.nanoTime)
      rand = srand.nextInt((max - min) + min)
    }
    rand
  }
  //обратный
  def inverse(a: Int, m: Int, x:Int = 1, y:Int = 0) : Int = if (m == 0) x else inverse(m, a%m, y, x - y*(a/m))

//  def getJSON(path:String, numClass:Int): Product with Serializable ={
//    val params = new File(path).getAbsolutePath
//    implicit val formats = DefaultFormats
//    var file: String = new String
//    Source.fromFile(params).foreach(s => file += s)
//
//    numClass match {
//      case 1 =>   parse(file).extract[ParamsL1]
//      case 2 =>   parse(file).extract[ParamsL2]
//      case 4 =>   parse(file).extract[ParamsL4]
//      case 5 =>    parse(file).extract[ParamsL5]
//      case 6 =>    parse(file).extract[ParamsL6]
//      case 7 =>   parse(file).extract[ParamsL7]
//    }
//  }
  def getBlock(n: Int, strListNum: String): Array[Int] = {
    strListNum.split(" ").map(word => word.toInt)
  }
}
