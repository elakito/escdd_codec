package de.elakito.escdd;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ESCDDCodec {
    private Pattern validCharRE;
    private Pattern validFirstCharRE;
    private boolean uppercase;
    private char escChar;

    public ESCDDCodec(String validCharRE, String validFirstCharRE, char escChar) {
        this(validCharRE, validFirstCharRE, escChar, false);
    }

    public ESCDDCodec(String validCharRE, String validFirstCharRE, char escChar, boolean uppercase) {
        this.validCharRE = Pattern.compile(validCharRE);
        this.validFirstCharRE = Pattern.compile(validFirstCharRE);
        this.uppercase = uppercase;
        if (!this.validCharRE.matcher(String.valueOf(escChar)).find()) {
            throw new RuntimeException(String.format("%c not among the valid characters %s", escChar, validCharRE));
        }
        if (!this.validFirstCharRE.matcher(String.valueOf(escChar)).find()) {
            throw new RuntimeException(String.format("%c not among the valid first characters %s", escChar, validFirstCharRE));
        }
        this.escChar = escChar;
    }

    public String encode(String value) {
        StringBuffer buf = new StringBuffer();
        boolean escaped = false;

        for (int i = 0; i < value.length(); i++) {
            Pattern re = i == 0 ? validFirstCharRE : validCharRE;
            String c = value.substring(i, i+1);
            if (value.charAt(i) != escChar && re.matcher(c).find()) {
                buf.append(value.charAt(i));
            } else {
                for (byte b : c.getBytes(UTF_8)) {
                    buf.append(String.format(uppercase ? "%c%X" : "%c%x", escChar, b));
                }
                escaped = true;
            }
        }
        return escaped ? buf.toString() : value;
    }

    public String decode(String value) {
        StringBuilder buf = new StringBuilder();
        ByteArrayOutputStream bdec = new ByteArrayOutputStream();
        if (value.indexOf(escChar) >= 0) {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == escChar) {
                    try {
                        bdec.write(Integer.parseInt(value.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        //ignore
                    }
                } else {
                    if (bdec.size() > 0) {
                        buf.append(bdec.toString(UTF_8));
                        bdec.reset();
                    }
                    buf.append(c);
                }
            }
            return buf.toString();
        } else {
            return value;
        }
    }
}
