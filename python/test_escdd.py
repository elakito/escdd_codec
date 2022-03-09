import unittest

import escdd
from escdd import escdd

class TestESCDD(unittest.TestCase):
    
    def test_escdd_urlencode(self):
        codec = escdd.ESCDDCodec("[A-Za-z0-9\-\._~%]", "[A-Za-z0-9\-\._~%]", "%", True)
        original = "happy:face?smile"
        expected = "happy%3Aface%3Fsmile"
        self.assertEqual(codec.encode(original), expected)
        self.assertEqual(codec.decode(expected), original)

    def test_escdd_urlencode_lowercase(self):
        codec = escdd.ESCDDCodec("[A-Za-z0-9\-\._~%]", "[A-Za-z0-9\-\._~%]", "%")
        original = "happy:face?smile"
        expected = "happy%3aface%3fsmile"
        self.assertEqual(codec.encode(original), expected)
        self.assertEqual(codec.decode(expected), original)

        expected2 = "happy%253aface%253fsmile"
        self.assertEqual(codec.encode(expected), expected2)
        self.assertEqual(codec.decode(expected2), expected)

    def test_escdd_avrolike(self):
        codec = escdd.ESCDDCodec("[A-Za-z0-9_]", "[A-Za-z_]", "_")
        original = "abc or いろは or 12_3"
        expected = "abc_20or_20_e3_81_84_e3_82_8d_e3_81_af_20or_2012_5f3"
        self.assertEqual(codec.encode(original), expected)
        self.assertEqual(codec.decode(expected), original)

        original = "123/abc"
        expected = "_3123_2fabc"
        self.assertEqual(codec.encode(original), expected)
        self.assertEqual(codec.decode(expected), original)

if __name__ == '__main__':
    unittest.main()
