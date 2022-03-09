import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import scala.collection.mutable.ArrayBuilder
import scala.io.Codec.UTF8

class ESCDDCodec(var validCharPattern: String, var validFirstCharPatter: String, var escChar: Char, var uppercase: Boolean = false) {
  private val validCharRE = validCharPattern.r
  private val validFirstCharRE = validFirstCharPatter.r

  require(validCharRE.findFirstIn(escChar.toString) != None, s"{escChar} not among the valid characters {validCharPattern}")
  require(validFirstCharRE.findFirstIn(escChar.toString) != None, s"{escChar} not among the valid first characters {validFirstCharPattern}")

  def encode(value: String): String = {
    val buf = new StringBuilder
    var escaped = false
    for (i <- 0 to value.length - 1) {
      val re = i match {
        case 0 => validFirstCharRE
        case _ => validCharRE
      }
      val c = value.substring(i, i+1)
      if (value.charAt(i) != escChar && re.findFirstIn(c) != None) {
        buf.append(c)
      } else {
        for (b <- c.getBytes(UTF_8)) {
          buf.append(if (uppercase) f"$escChar%c$b%X" else f"$escChar%c$b%x")
        }
        escaped = true
      }
    }
    escaped match {
      case true => buf.toString
      case _ => value
    }
  }

  def decode(value: String): String = {
    val buf = new StringBuilder
    var bdec = new ArrayBuilder.ofByte
    if (value.contains(escChar)) {
      var i = 0
      while (i < value.length) {
        val c = value.charAt(i)
        if (c == escChar) {
          bdec.addOne(Integer.parseInt(value.substring(i + 1, i + 3), 16).toByte)
          i += 2
        } else {
          if (bdec.length > 0) {
            buf.append(new String(bdec.result, StandardCharsets.UTF_8))
            bdec.clear
          }
           buf.append(c)
        }
        i += 1
      }
      return buf.toString
    } else {
      return value
    }
  }
}
