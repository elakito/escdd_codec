package de.elakito.escdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

class ESCDDCodecTest {

    @ParameterizedTest(name = "urlencode")
    @ValueSource(strings = {
            "happy:face?smile",
            "happy%3Aface%3Fsmile",
    })
    void escdd_urlencode(String input) {
        ESCDDCodec codec = new ESCDDCodec("[A-Za-z0-9\\-\\._~%]", "[A-Za-z0-9\\-\\._~%]", '%', true);
        String expected = URLEncoder.encode(input, StandardCharsets.UTF_8);
        // verify the encoding and decoding
        assertEquals(expected, codec.encode(input));
        assertEquals(input, codec.decode(expected));
    }

    @ParameterizedTest(name = "urlencode-lowercase")
    @CsvSource({
            "'happy:face?smile/laugh,laugh', 'happy%3aface%3fsmile%2flaugh%2claugh'"
    })
    void escdd_urlencode_lowercase(String input, String expected) {
        ESCDDCodec codec = new ESCDDCodec("[A-Za-z0-9\\-\\._~%]", "[A-Za-z0-9\\-\\._~%]", '%');
        // verify the encoding and decoding
        assertEquals(expected, codec.encode(input));
        assertEquals(input, codec.decode(expected));
    }

    @ParameterizedTest(name = "avrolike-encode")
    @CsvSource({
            "'happy:face?smile/laugh,laugh', 'happy_3aface_3fsmile_2flaugh_2claugh'",
            "'abc or いろは or 12_3', 'abc_20or_20_e3_81_84_e3_82_8d_e3_81_af_20or_2012_5f3'",
            "'123/abc', '_3123_2fabc'"
    })
    void escdd_avrolike(String input, String expected) {
        ESCDDCodec codec = new ESCDDCodec("[A-Za-z0-9_]", "[A-Za-z_]", '_');
        // verify the encoding and decoding
        assertEquals(expected, codec.encode(input));
        assertEquals(input, codec.decode(expected));
    }
}
