package simple

import java.io.{ByteArrayOutputStream, DataOutputStream}

import scala.annotation.implicitNotFound

@implicitNotFound(msg = "Could not find a Writes for ${T}")
trait Writes[T] {

  def writes(value: T): Array[Byte]
}

class DataOutputStreamWrites[T](writeValue: (DataOutputStream, T) => Unit) extends Writes[T] {

  def writes(value: T): Array[Byte] = {
    val bos = new ByteArrayOutputStream
    val dos = new DataOutputStream(bos)
    writeValue(dos, value)
    dos.flush()
    val byteArray = bos.toByteArray
    bos.close()
    byteArray
  }
}

object defaults {

  implicit object WritesString extends Writes[String] {
    def writes(value: String) = value.getBytes("UTF-8")
  }

  implicit object WritesLong extends DataOutputStreamWrites[Long](_.writeLong(_))

  implicit object WritesInt extends DataOutputStreamWrites[Int](_.writeInt(_))

  implicit object WritesShort extends DataOutputStreamWrites[Short](_.writeShort(_))

}
