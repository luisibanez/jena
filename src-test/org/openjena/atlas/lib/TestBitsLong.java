/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.openjena.atlas.lib;

import org.junit.Test;
import org.openjena.atlas.junit.BaseTest ;
import org.openjena.atlas.lib.BitsLong ;


public class TestBitsLong extends BaseTest
{
    @Test public void testMask1()
    {
        long v = BitsLong.mask(0,1) ;
        check(0x1L, v) ;
    }
    
    @Test public void testMask2()
    {
        long v = BitsLong.mask(0,2) ;
        check(0x3L, v) ;
    }
    
    @Test public void testMask3()
    {
        long v = BitsLong.mask(1,2) ;
        check(0x2L, v) ;
    }
    
    @Test public void testMask4()
    {
        long v = BitsLong.mask(0,64) ;
        check(-1L, v) ;
    }
    
    @Test public void testMask5()
    {
        long v = BitsLong.mask(16,48) ;
        check(0x0000FFFFFFFF0000L, v) ;
    }

    @Test public void testMask6()
    {
        long v = BitsLong.mask(16,64) ;
        check(0xFFFFFFFFFFFF0000L, v) ;
    }

    @Test public void testMask7()
    {
        long v = BitsLong.mask(0, 0) ;
        check(0L, v) ;
    }
    
    @Test public void testMaskZero1()
    {
        long v = BitsLong.maskZero(0,1) ;
        check(~0x1L, v) ;
    }
    
    @Test public void testMaskZero2()
    {
        long v = BitsLong.maskZero(0,2) ;
        check(~0x3L, v) ;
    }
    
    @Test public void testMaskZero3()
    {
        long v = BitsLong.maskZero(1,2) ;
        check(0xFFFFFFFFFFFFFFFDL, v) ;
    }
    
    @Test public void testMaskZero4()
    {
        long v = BitsLong.maskZero(0,64) ;
        check(0, v) ;
    }
    
    @Test public void testMaskZero5()
    {
        long v = BitsLong.maskZero(16,48) ;
        check(0xFFFF00000000FFFFL, v) ;
    }

    @Test public void testMaskZero6()
    {
        long v = BitsLong.maskZero(16,64) ;
        check(0xFFFFL, v) ;
    }

    @Test public void testMaskZero7()
    {
        long v = BitsLong.maskZero(0, 0) ;
        check(-1L, v) ;
    }

    @Test public void testClear1()
    {
        long v = 0xF0F0 ;
        v = BitsLong.clear(v, 4, 8) ;
        String s = Long.toHexString(v) ;
        check(0xF000L, v ) ;
    }

    @Test public void testClear2()
    {
        long v = 0x8000000000000000L;
        v = BitsLong.clear(v, 63, 64) ;
        String s = Long.toHexString(v) ;
        check(0x0L, v ) ;
    }

    @Test public void testClear3()
    {
        long v = 0xC000000000000000L;
        v = BitsLong.clear(v, 63, 64) ;
        String s = Long.toHexString(v) ;
        check(0x4000000000000000L, v ) ;
    }

    @Test public void testClear4()
    {
        long v = -1 ;
        v = BitsLong.clear(v, 63, 64) ;
        String s = Long.toHexString(v) ;
        check(0x7FFFFFFFFFFFFFFFL, v ) ;
    }
    
    @Test public void testClear5()
    {
        long v = -1 ;
        v = BitsLong.clear(v, 32, 64) ;
        String s = Long.toHexString(v) ;
        check(0x00000000FFFFFFFFL, v ) ;
    }

    @Test public void testClear6()
    {
        long v = -1 ;
        v = BitsLong.clear(v, 0, 32) ;
        String s = Long.toHexString(v) ;
        check(0xFFFFFFFF00000000L, v ) ;
    }

    @Test public void testClear7()
    {
        long v = -1L ;
        v = BitsLong.clear(v, 0, 0) ;
        String s = Long.toHexString(v) ;
        check(-1L, v ) ;
    }

    @Test public void testSet1()
    {
        long v = 0x0 ;
        v = BitsLong.set(v, 0, 1) ;
        check(1, v) ;
    }
    
    @Test public void testSet2()
    {
        long v = 0x1 ;
        v = BitsLong.set(v, 0, 1) ;
        check(1, v) ;
    }
    
    @Test public void testSet3()
    {
        long v = 0xF0 ;
        v = BitsLong.set(v, 0, 1) ;
        check(0xF1, v) ;
    }
    
    @Test public void testSet4()
    {
        long v = 0xF0F0F0F0F0F0F0F0L ;
        v = BitsLong.set(v, 0, 8) ;
        check(0xF0F0F0F0F0F0F0FFL, v) ;
    }

    @Test public void testSet5()
    {
        long v = 0 ;
        v = BitsLong.set(v, 16, 48) ;
        check(0x0000FFFFFFFF0000L, v) ;
    }
    
    @Test public void testSet6()
    {
        long v = 0 ;
        v = BitsLong.set(v, 63, 64) ;
        check(0x8000000000000000L, v) ;
    }
    
    @Test public void testSet7()
    {
        long v = 0 ;
        v = BitsLong.set(v, 62, 64) ;
        check(0xC000000000000000L, v) ;
    }
    
    @Test public void testSet8()
    {
        long v = 0 ;
        v = BitsLong.set(v, 0, 64) ;
        check(-1L, v) ;
    }
    
    @Test public void testSet9()
    {
        long v = 0 ;
        v = BitsLong.set(v, 10, 10) ;
        check(0, v) ;
    }
    
    @Test public void testSetBit1()
    {
        long v = 0 ;
        v = BitsLong.set(v, 0) ;
        check(1, v) ;
    }
    
    @Test public void testSetBit2()
    {
        long v = 0 ;
        v = BitsLong.set(v, 1) ;
        check(2, v) ;
    }
    
    @Test public void testSetBit3()
    {
        long v = 1 ;
        v = BitsLong.set(v, 0) ;
        check(1, v) ;
    }

    @Test public void testSetBit4()
    {
        long v = -1 ;
        v = BitsLong.set(v, 0) ;
        check(-1, v) ;
    }

    @Test public void testSetBit5()
    {
        long v = 0 ;
        v = BitsLong.set(v, 62) ;
        check(0x4000000000000000L, v) ;
    }

    @Test public void testSetBit6()
    {
        long v = 0 ;
        v = BitsLong.set(v, 63) ;
        check(0x8000000000000000L, v) ;
    }
    
    @Test public void testBitTest1()
    {
        long v = 0 ;
        assertTrue(BitsLong.test(v, false, 0)) ;
    }
    
    @Test public void testBitTest2()
    {
        long v = 1 ;
        assertTrue(BitsLong.test(v, true, 0)) ;
    }
    
    @Test public void testBitTest3()
    {
        long v = -1 ;
        assertTrue(BitsLong.test(v, true, 63)) ;
    }

    @Test public void testBitTest4()
    {
        long v = 0x7FFFFFFFFFFFFFFFL ;
        assertTrue(BitsLong.test(v, false, 63)) ;
    }

    @Test public void testBitsTest1()
    {
        long v = 0xFEDCBA9876543210L ;
        assertTrue(BitsLong.test(v, 0x0, 0, 4)) ;
    }
    
    @Test public void testBitsTest2()
    {
        long v = 0xFEDCBA9876543210L ;
        assertTrue(BitsLong.test(v, 0x10, 0, 8)) ;
    }
    
    @Test public void testBitsTest3()
    {
        long v = 0xFEDCBA9876543210L ;
        assertTrue(BitsLong.test(v, v, 0, 64)) ;
    }
    
    @Test public void testBitsTest4()
    {
        long v = 0xFEDCBA9876543210L ;
        assertFalse(BitsLong.test(v, 0, 0, 64)) ;
    }
    
    @Test public void testBitsTest5()
    {
        long v = 0xFEDCBA9876543210L ;
        assertTrue(BitsLong.test(v, 0x0000BA9876540000L, 16, 48)) ;
    }
    
    @Test public void testIsSet1()
    {
        long v = 0x0000000000000010L ;
        BitsLong.isSet(v, 4) ;
        assertTrue(BitsLong.isSet(v, 4)) ;
        assertFalse(BitsLong.isSet(v, 3)) ;
        assertFalse(BitsLong.isSet(v, 5)) ;
    }
    
    @Test public void testAccess1()
    {
        long v = -1 ;
        v = BitsLong.access(v, 4, 8) ;
        check(0xF0L, v ) ;
    }
    
    @Test public void testAccess2()
    {
        long v = 0xFEDCBA9876543210L ;
        v = BitsLong.access(v, 0, 8) ;
        check(0x10L, v ) ;
    }

    @Test public void testAccess3()
    {
        long v = 0xFEDCBA9876543210L ;
        v = BitsLong.access(v, 0, 64) ;
        check(0xFEDCBA9876543210L, v ) ;
    }

    @Test public void testAccess4()
    {
        long v = 0xFEDCBA9876543210L ;
        v = BitsLong.access(v, 62, 64) ;
        check(0xC000000000000000L, v ) ;
    }

    @Test public void testAccess5()
    {
        long v = 0xFEDCBA9876543210L ;
        v = BitsLong.access(v, 0, 2) ;
        check(0L, v ) ;
    }

    @Test public void testPack1()
    {
        long v = 0 ;
        v = BitsLong.pack(v, 0xFL, 0, 4) ;
        check(0xFL, v ) ;
    }
    
    @Test public void testPack2()
    {
        long v = 0xF0 ;
        v = BitsLong.pack(v, 0x2, 0, 4) ;
        check(0xF2L, v ) ;
    }
    
    @Test public void testPack3()
    {
        long v = -1 ;
        v = BitsLong.pack(v, 0x2, 0, 8) ;
        check(0xFFFFFFFFFFFFFF02L, v ) ;
    }

    @Test public void testPack4()
    {
        long v = 0xFFFFFFFF00000000L ;
        v = BitsLong.pack(v, 0x2, 16, 32) ;
        check(0xFFFFFFFF00020000L, v ) ;
    }

    @Test public void testPack5()
    {
        long v = 0xFFFFFFFF00000000L ;
        v = BitsLong.pack(v, 0xFFFF, 16, 32) ;
        check(0xFFFFFFFFFFFF0000L, v ) ;
    }

    @Test public void testUnpack1()
    {
        long v = 0xABCDABCDABCDABCDL ;
        v = BitsLong.unpack(v, 0, 4) ;
        check(0xDL, v ) ;
    }
    
    @Test public void testUnpack2()
    {
        long v = 0xABCDABCDABCDABCDL ;
        v = BitsLong.unpack(v, 63, 64) ;
        check(1L, v ) ;
    }

    @Test public void testUnpack3()
    {
        long v = 0xABCDABCDABCDABCDL ;
        v = BitsLong.unpack(v, 56, 64) ;
        check(0xABL, v ) ;
    }

    @Test public void testUnpack4()
    {
        long v = 0xABCD12345678ABCDL ;
        v = BitsLong.unpack(v, 32, 48) ;
        check(0x1234L, v ) ;
    }

    @Test public void testUnpackStr1()
    {
        String s = "ABCD" ;
        long v = BitsLong.unpack(s, 0, 4) ;
        check(0xABCDL, v ) ;
    }
    
    @Test public void testUnpackStr2()
    {
        String s = "ABCD" ;
        long v = BitsLong.unpack(s, 2, 4) ;
        check(0xCDL, v ) ;
    }
    
    @Test public void testUnpackStr3()
    {
        String s = "ABCD" ;
        long v = BitsLong.unpack(s, 0, 2) ;
        check(0xABL, v ) ;
    }
    
    private void check(long expected, long actual)
    {
        check(null, expected, actual) ;
    }
    
    private void check(String msg, long expected, long actual)
    {
        if ( expected == actual ) return ;
        String s = "Expected: "+Long.toHexString(expected)+" : Got: "+Long.toHexString(actual) ;
        if ( msg != null )
            s = msg+": "+s ;
        assertFalse(s, true) ;
    }
}

/*
 * (c) Copyright 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */