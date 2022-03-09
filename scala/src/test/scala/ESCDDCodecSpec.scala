import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ESCDDCodecSpec extends AnyFlatSpec with should.Matchers {

  it should "behave like urlencode" in {
    val codec = new ESCDDCodec("[A-Za-z0-9\\-\\._~%]", "[A-Za-z0-9\\-\\._~%]", '%', true)
    urlencode(codec, "happy:face?smile")
    urlencode(codec, "happy%3Aface%3Fsmile")
  }

  it should "behave like urlncode with lowercase" in {
    val codec = new ESCDDCodec("[A-Za-z0-9\\-\\._~%]", "[A-Za-z0-9\\-\\._~%]", '%')
    verifycodec(codec, "happy:face?smile/laugh,laugh", "happy%3aface%3fsmile%2flaugh%2claugh")
  }

  it should "behave like avrolike" in {
    val codec = new ESCDDCodec("[A-Za-z0-9_]", "[A-Za-z_]", '_')
    verifycodec(codec, "happy:face?smile/laugh,laugh", "happy_3aface_3fsmile_2flaugh_2claugh")
    verifycodec(codec, "abc or いろは or 12_3", "abc_20or_20_e3_81_84_e3_82_8d_e3_81_af_20or_2012_5f3")
    verifycodec(codec, "123/abc", "_3123_2fabc")
  }

  def urlencode(codec: ESCDDCodec, input: String): Unit = {
    verifycodec(codec, input, URLEncoder.encode(input, StandardCharsets.UTF_8))
  }

  def verifycodec(codec: ESCDDCodec, input: String, expected: String): Unit = {
    codec.encode(input) should be (expected)
    codec.decode(expected) should be (input)
  }
}
