/*
 * Copyright (c) 1999-2004 Igor Pavlov
 *
 * Modified by Naohide Sano
 */

package vavi.util.lzma;

import java.io.IOException;
import java.io.InputStream;


/**
 * RangeDecoder.
 * 
 * @author Igor Pavlov
 * @author myspace
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040916 nsano modified <br>
 */
class RangeDecoder {
    /** */
    private static final int NumberTopBits = 24;
    /** */
    private final static int TopValue = (1 << NumberTopBits);
    /** */
    private final static int NumberBitModelTotalBits = 11;
    /** */
    public final static int BitModelTotal = (1 << NumberBitModelTotalBits);
    /** */
    private final static int NumberMoveBits = 5;
    /** */
    private InputStream is;
    /** */
    private long range;
    /** */
    private long code;
    /** */
    private byte[] buffer;
    /** */
    private int bufferSize;
    /** */
    private int bufferIndex;

    /** */
    RangeDecoder(InputStream iStream) throws IOException {
        this.buffer = new byte[1 << 14];
        this.is = iStream;
        this.code = 0;
        this.range = 0xFFFFFFFFL;
        for (int i = 0; i < 5; i++) {
            this.code = (this.code << 8) | (readByte());
        }
    }

    /** */
    int readByte() throws IOException {
        if (bufferSize == bufferIndex) {
            bufferSize = is.read(buffer);
            bufferIndex = 0;

            if (bufferSize < 1) {
                throw new LzmaException("LZMA : Data Error");
            }
        }
        return buffer[bufferIndex++] & 0xFF;
    }

    /** */
    int decodeDirectBits(int numTotalBits) throws IOException {
        int result = 0;
        for (int i = numTotalBits; i > 0; i--) {
            this.range >>= 1;

            result <<= 1;
            if (this.code >= this.range) {
                this.code -= this.range;
                result |= 1;
            }

            if (this.range < TopValue) {
                this.range <<= 8;
                this.code = (this.code << 8) | (readByte());
            }
        }
        return result;
    }

    /** */
    int decodeBit(int[] prob, int index) throws IOException {
        long bound = (this.range >> NumberBitModelTotalBits) * prob[index];
        if (this.code < bound) {
            this.range = bound;
            prob[index] += ((BitModelTotal - prob[index]) >> NumberMoveBits);
            if (this.range < TopValue) {
                this.code = (this.code << 8) | (readByte());
                this.range <<= 8;
            }
            return 0;
        } else {
            this.range -= bound;
            this.code -= bound;
            prob[index] -= ((prob[index]) >> NumberMoveBits);
            if (this.range < TopValue) {
                this.code = (this.code << 8) | (readByte());
                this.range <<= 8;
            }
            return 1;
        }
    }

    /** */
    int decodeBitTree(int[] probs, int index, int numLevels)
        throws IOException {
        int mi = 1;
        for (int i = numLevels; i > 0; i--) {
            mi = (mi + mi) + decodeBit(probs, index + mi);
        }
        return mi - (1 << numLevels);
    }

    /** */
    int decodeReverseBitTree(int[] probs, int index, int numLevels)
        throws IOException {
        int mi = 1;
        int symbol = 0;

        for (int i = 0; i < numLevels; i++) {
            int bit = decodeBit(probs, index + mi);
            mi = mi + mi + bit;
            symbol |= (bit << i);
        }
        return symbol;
    }

    /** */
    byte decodeLzmaLiteral(int[] probs, int index) throws IOException {
        int symbol = 1;
        do {
            symbol = (symbol + symbol) | decodeBit(probs, index + symbol);
        } while (symbol < 0x100);

        return (byte) symbol;
    }

    /** */
    byte decodeLzmaLiteralMatch(int[] probs, int index, byte matchbyte)
        throws IOException {
        int symbol = 1;
        do {
            int matchBit = (matchbyte >> 7) & 1;
            matchbyte <<= 1;

            int bit = decodeBit(probs, index + ((1 + matchBit) << 8) + symbol);
            symbol = (symbol << 1) | bit;

            if (matchBit != bit) {
                while (symbol < 0x100) {
                    symbol = (symbol + symbol) |
                             decodeBit(probs, index + symbol);
                }
                break;
            }
        } while (symbol < 0x100);

        return (byte) symbol;
    }

    /** */
    public final static int NumberPositionBitsMaximum = 4;
    /** */
    private final static int NumberPositionStatesMaximum = (1 << NumberPositionBitsMaximum);
    /** */
    private final static int LengthNumberLowBits = 3;
    /** */
    private final static int LengthNumberLowSymbols = (1 << LengthNumberLowBits);
    /** */
    private final static int LengthNumberMiddleBits = 3;
    /** */
    private final static int LengthNumberMiddleSymbols = (1 << LengthNumberMiddleBits);
    /** */
    private final static int LengthNumberHighBits = 8;
    /** */
    private final static int LengthNumberHighSymbols = (1 << LengthNumberHighBits);
    /** */
    private final static int LenChoice = 0;
    /** */
    private final static int LenChoice2 = (LenChoice + 1);
    /** */
    private final static int LenLow = (LenChoice2 + 1);
    /** */
    private final static int LenMiddle = (LenLow + (NumberPositionStatesMaximum << LengthNumberLowBits));
    /** */
    private final static int LenHigh = (LenMiddle + (NumberPositionStatesMaximum << LengthNumberMiddleBits));
    /** */
    public final static int NumberLengthProbs = (LenHigh + LengthNumberHighSymbols);

    /** */
    int LzmaLenDecode(int[] probs, int index, int posState)
        throws IOException {
        if (decodeBit(probs, index + LenChoice) == 0) {
            return decodeBitTree(probs,
                                 index + LenLow + (posState << LengthNumberLowBits),
                                 LengthNumberLowBits);
        }

        if (decodeBit(probs, index + LenChoice2) == 0) {
            return LengthNumberLowSymbols +
                   decodeBitTree(probs,
                                 index + LenMiddle + (posState << LengthNumberMiddleBits),
                                 LengthNumberMiddleBits);
        }

        return LengthNumberLowSymbols + LengthNumberMiddleSymbols +
               decodeBitTree(probs, index + LenHigh, LengthNumberHighBits);
    }
}

/* */
