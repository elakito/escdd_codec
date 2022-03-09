#!/usr/bin/python3

import re

class ESCDDCodec:
    
    def __init__(self, valid_char_pattern, valid_first_char_pattern, esc_char, uppercase=False):
        self.valid_char_re = re.compile(valid_char_pattern)
        self.valid_first_char_re = re.compile(valid_first_char_pattern)
        self.uppercase = uppercase
        if not self.valid_char_re.search(esc_char):
            raise ValueError(f"{esc_char} not among the valid characters {valid_char_pattern}")
        if not self.valid_first_char_re.search(esc_char):
            raise ValueError(f"{esc_char} not among the valid first characters {valid_first_char_pattern}")
        self.esc_char = esc_char

    def encode(self, value):
        buf = []
        escaped = False
        first_char = True
        for c in value:
            rx = self.valid_first_char_re if first_char else self.valid_char_re
            if c != self.esc_char and rx.search(c):
                buf.append(c)
            else:
                buf.append(self.esc_char)
                benc = c.encode().hex(self.esc_char)
                if self.uppercase:
                    benc = benc.upper()
                buf.append(benc)
                escaped = True
            first_char = False
        if escaped:
            return "".join(buf)
        else:
            return value

    def decode(self, value):
        if self.esc_char in value:
            buf = []
            inescape = 0
            bdec = 0
            for c in value:
                if c == self.esc_char:
                    inescape = 2
                    bdec = 0
                elif inescape > 0:
                    bdec = (bdec << 4) + int(c, 16)
                    inescape -= 1
                    if inescape == 0:
                        buf.append(bdec)
                else:
                    buf.append(ord(c))
            return bytes(buf).decode()
        else:
            return value
        
