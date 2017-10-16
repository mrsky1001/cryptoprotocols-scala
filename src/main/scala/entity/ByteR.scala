package entity

import java.util

class ByteR(byte: Byte) {
  var strBits: String = new String
  var bits: util.BitSet = util.BitSet.valueOf(Array(byte))
  var countUnits: Int = 0
  var countNulls: Int = 0
  var indexLastBit: Int = 0
  (0 until 8).foreach(i =>
    if (this.bits.get(i)) {
      this.strBits += "1"
      this.indexLastBit = (i - 7) % 8 * (-1)
      this.countUnits += 1
    }
    else {
      this.countNulls += 1
      this.strBits += "0"
    })

}
