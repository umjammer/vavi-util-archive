/*
 * Copyright (c) 1999-2004 Igor Pavlov
 *
 * Modified by Naohide Sano
 */

package vavi.util.codec.lzma;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;


/**
 * LzmaInputStream.
 *
 * @author Igor Pavlov
 * @author myspace
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040916 nsano modified <br>
 */
public class LzmaInputStream extends FilterInputStream {
    /** */
    private boolean closed;
    /** */
    private RangeDecoder rangeDecoder;
    /** */
    private byte[] dictionary;
    /** */
    private int dictionarySize;
    /** */
    private int dictionaryPos;
    /** */
    private int globalPosition;
    /** */
    private int rep0;
    /** */
    private int rep1;
    /** */
    private int rep2;
    /** */
    private int rep3;
    /** */
    private int lc;
    /** */
    private int lp;
    /** */
    private int pb;
    /** */
    private int state;
    /** */
    private boolean previousIsMatch;
    /** */
    private int RemainLen;
    /** */
    private int[] probs;
    /** */
    private byte[] uncompressedBuffer;
    /** */
    private int uncompressedSize;
    /** */
    private int uncompressedOffset;
    /** */
    private long globalNowPosition;
    /** */
    private long globalOutSize;
    /** */
    private static final int LZMA_BASE_SIZE = 1846;
    /** */
    private static final int LZMA_LIT_SIZE = 768;
    /** */
    private static final int BlockSize = 0x10000;
    /** */
    private static final int NumberStates = 12;
    /** */
    private static final int StartPositionModelIndex = 4;
    /** */
    private static final int EndPositionModelIndex = 14;
    /** */
    private static final int NumberFullDistances = (1 << (EndPositionModelIndex >> 1));
    /** */
    private static final int NumberPositionSlotBits = 6;
    /** */
    private static final int NumberLengthToPositionStates = 4;
    /** */
    private static final int NumberAlignBits = 4;
    /** */
    private static final int AlignTableSize = (1 << NumberAlignBits);
    /** */
    private static final int MatchMinmumLength = 2;
    /** */
    private static final int IsMatch = 0;
    /** */
    private static final int IsRep = (IsMatch +
                             (NumberStates << RangeDecoder.NumberPositionBitsMaximum));
    /** */
    private static final int IsRepG0 = (IsRep + NumberStates);
    /** */
    private static final int IsRepG1 = (IsRepG0 + NumberStates);
    /** */
    private static final int IsRepG2 = (IsRepG1 + NumberStates);
    /** */
    private static final int IsRep0Long = (IsRepG2 + NumberStates);
    /** */
    private static final int PosSlot = (IsRep0Long +
                               (NumberStates << RangeDecoder.NumberPositionBitsMaximum));
    /** */
    private static final int SpecPos = (PosSlot +
                               (NumberLengthToPositionStates << NumberPositionSlotBits));
    /** */
    private static final int Align = ((SpecPos + NumberFullDistances) -
                             EndPositionModelIndex);
    /** */
    private static final int LenCoder = (Align + AlignTableSize);
    /** */
    private static final int RepLenCoder = (LenCoder + RangeDecoder.NumberLengthProbs);
    /** */
    private static final int Literal = (RepLenCoder + RangeDecoder.NumberLengthProbs);

    /** */
    public LzmaInputStream(InputStream in) throws IOException {
        super(in);

        closed = false;

        readHeader();

        fillBuffer();
    }

    /** */
    private void decodeLzma(int outSize) throws IOException {
        byte previousbyte;
        int posStateMask = (1 << (pb)) - 1;
        int literalPosMask = (1 << (lp)) - 1;

        uncompressedSize = 0;

        if (RemainLen == -1) {
            return;
        }

        while ((RemainLen > 0) && (uncompressedSize < outSize)) {
            int pos = dictionaryPos - rep0;
            if (pos < 0) {
                pos += dictionarySize;
            }
            uncompressedBuffer[uncompressedSize++] = dictionary[dictionaryPos] = dictionary[pos];
            if (++dictionaryPos == dictionarySize) {
                dictionaryPos = 0;
            }
            RemainLen--;
        }
        if (dictionaryPos == 0) {
            previousbyte = dictionary[dictionarySize - 1];
        } else {
            previousbyte = dictionary[dictionaryPos - 1];
        }

        while (uncompressedSize < outSize) {
            int posState = ((uncompressedSize + globalPosition) & posStateMask);

            if (rangeDecoder.decodeBit(probs,
                                       IsMatch +
                                       (state << RangeDecoder.NumberPositionBitsMaximum) +
                                       posState) == 0) {
                int ind_prob = Literal +
                               (LZMA_LIT_SIZE * ((((uncompressedSize +
                               globalPosition) & literalPosMask) << lc) +
                               ((previousbyte & 0xFF) >> (8 - lc))));

                if (state < 4) {
                    state = 0;
                } else if (state < 10) {
                    state -= 3;
                } else {
                    state -= 6;
                }
                if (previousIsMatch) {
                    int pos = dictionaryPos - rep0;
                    if (pos < 0) {
                        pos += dictionarySize;
                    }

                    byte matchbyte = dictionary[pos];

                    previousbyte = rangeDecoder.decodeLzmaLiteralMatch(probs,
                                                                       ind_prob,
                                                                       matchbyte);
                    previousIsMatch = false;
                } else {
                    previousbyte = rangeDecoder.decodeLzmaLiteral(probs,
                                                                  ind_prob);
                }

                uncompressedBuffer[uncompressedSize++] = previousbyte;

                dictionary[dictionaryPos] = previousbyte;
                if (++dictionaryPos == dictionarySize) {
                    dictionaryPos = 0;
                }
            } else {
                previousIsMatch = true;
                if (rangeDecoder.decodeBit(probs, IsRep + state) == 1) {
                    if (rangeDecoder.decodeBit(probs, IsRepG0 + state) == 0) {
                        if (rangeDecoder.decodeBit(probs,
                                                   IsRep0Long +
                                                   (state << RangeDecoder.NumberPositionBitsMaximum) +
                                                   posState) == 0) {
                            if ((uncompressedSize + globalPosition) == 0) {
                                throw new LzmaException("LZMA : Data Error");
                            }
                            state = (state < 7) ? 9 : 11;

                            int pos = dictionaryPos - rep0;
                            if (pos < 0) {
                                pos += dictionarySize;
                            }
                            previousbyte = dictionary[pos];
                            dictionary[dictionaryPos] = previousbyte;
                            if (++dictionaryPos == dictionarySize) {
                                dictionaryPos = 0;
                            }

                            uncompressedBuffer[uncompressedSize++] = previousbyte;
                            continue;
                        }
                    } else {
                        int distance;
                        if (rangeDecoder.decodeBit(probs, IsRepG1 + state) == 0) {
                            distance = rep1;
                        } else {
                            if (rangeDecoder.decodeBit(probs, IsRepG2 + state) == 0) {
                                distance = rep2;
                            } else {
                                distance = rep3;
                                rep3 = rep2;
                            }
                            rep2 = rep1;
                        }
                        rep1 = rep0;
                        rep0 = distance;
                    }
                    RemainLen = rangeDecoder.LzmaLenDecode(probs, RepLenCoder,
                                                           posState);
                    state = (state < 7) ? 8 : 11;
                } else {
                    rep3 = rep2;
                    rep2 = rep1;
                    rep1 = rep0;
                    state = (state < 7) ? 7 : 10;
                    RemainLen = rangeDecoder.LzmaLenDecode(probs, LenCoder,
                                                           posState);

                    int posSlot = rangeDecoder.decodeBitTree(probs,
                                                             PosSlot +
                                                             (((RemainLen < NumberLengthToPositionStates)
                                                               ? RemainLen
                                                               : (NumberLengthToPositionStates -
                                                               1)) << NumberPositionSlotBits),
                                                             NumberPositionSlotBits);
                    if (posSlot >= StartPositionModelIndex) {
                        int numDirectBits = ((posSlot >> 1) - 1);
                        rep0 = ((2 | (posSlot & 1)) << numDirectBits);
                        if (posSlot < EndPositionModelIndex) {
                            rep0 += rangeDecoder.decodeReverseBitTree(probs,
                                                                      (SpecPos +
                                                                      rep0) -
                                                                      posSlot -
                                                                      1,
                                                                      numDirectBits);
                        } else {
                            rep0 += (rangeDecoder.decodeDirectBits(numDirectBits -
                                                                   NumberAlignBits) << NumberAlignBits);
                            rep0 += rangeDecoder.decodeReverseBitTree(probs,
                                                                      Align,
                                                                      NumberAlignBits);
                        }
                    } else {
                        rep0 = posSlot;
                    }
                    rep0++;
                }
                if (rep0 == 0) {
                    RemainLen = -1;
                    break;
                }
                if (rep0 > (uncompressedSize + globalPosition)) {
                    throw new LzmaException("LZMA : Data Error");
                }
                RemainLen += MatchMinmumLength;
                do {
                    int pos = dictionaryPos - rep0;
                    if (pos < 0) {
                        pos += dictionarySize;
                    }
                    previousbyte = dictionary[pos];
                    dictionary[dictionaryPos] = previousbyte;
                    if (++dictionaryPos == dictionarySize) {
                        dictionaryPos = 0;
                    }

                    uncompressedBuffer[uncompressedSize++] = previousbyte;
                    RemainLen--;
                } while ((RemainLen > 0) && (uncompressedSize < outSize));
            }
        }

        globalPosition = globalPosition + uncompressedSize;
    }

    /** */
    private void fillBuffer() throws IOException {
        if (globalNowPosition < globalOutSize) {
            uncompressedOffset = 0;

            long lblockSize = globalOutSize - globalNowPosition;
            int blockSize;
            if (lblockSize > BlockSize) {
                blockSize = BlockSize;
            } else {
                blockSize = (int) lblockSize;
            }

            decodeLzma(blockSize);

            if (uncompressedSize == 0) {
                globalOutSize = globalNowPosition;
            } else {
                globalNowPosition += uncompressedSize;
            }
        }
    }

    /** */
    private void readHeader() throws IOException {
        byte[] properties = new byte[5];

        if (5 != in.read(properties)) {
            throw new LzmaException("LZMA header corrupted : Properties error");
        }

        globalOutSize = 0;
        for (int ii = 0; ii < 8; ii++) {
            int b = in.read();
            if (b == -1) {
                throw new LzmaException("LZMA header corrupted : Size error");
            }
            globalOutSize += (((long) b) << (ii * 8));
        }

        int prop0 = properties[0] & 0xFF;
        if (prop0 >= (9 * 5 * 5)) {
            throw new LzmaException("LZMA header corrupted : Properties error");
        }

        for (pb = 0; prop0 >= (9 * 5); pb++, prop0 -= (9 * 5)) {
        }
        for (lp = 0; prop0 >= 9; lp++, prop0 -= 9) {
        }
        lc = prop0;

        int lzmaInternalSize = (LZMA_BASE_SIZE + (LZMA_LIT_SIZE << (lc + lp)));

        probs = new int[lzmaInternalSize];

        dictionarySize = 0;
        for (int i = 0; i < 4; i++) {
            dictionarySize += ((properties[1 + i] & 0xFF) << (i * 8));
        }
        dictionary = new byte[dictionarySize];

        int numProbs = Literal + (LZMA_LIT_SIZE << (lc + lp));

        rangeDecoder = new RangeDecoder(in);
        dictionaryPos = 0;
        globalPosition = 0;
        rep0 = rep1 = rep2 = rep3 = 1;
        state = 0;
        previousIsMatch = false;
        RemainLen = 0;
        dictionary[dictionarySize - 1] = 0;
        for (int i = 0; i < numProbs; i++) {
            probs[i] = RangeDecoder.BitModelTotal >> 1;
        }

        uncompressedBuffer = new byte[BlockSize];
        uncompressedSize = 0;
        uncompressedOffset = 0;

        globalNowPosition = 0;
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        if (closed) {
            throw new IOException("stream closed");
        }

        if ((off | len | (off + len) | (buf.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }

        if (uncompressedOffset == uncompressedSize) {
            fillBuffer();
        }
        if (uncompressedOffset == uncompressedSize) {
            return -1;
        }

        int l = Math.min(len, uncompressedSize - uncompressedOffset);
        System.arraycopy(uncompressedBuffer, uncompressedOffset, buf, off, l);
        uncompressedOffset += l;
        return l;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        super.close();
    }

    //----

    /** */
    public static void main(String[] args) {
        System.out.println("\nLZMA Decoder 4.02 Copyright (c) 1999-2004 Igor Pavlov  2004-06-10");
        System.out.println("JAVA LZMA Decoder 0.90  myspace\n");

        if ((args.length < 1) || (args.length > 2)) {
            System.out.println("\nUsage:  lzmaTest file.lzma [outFile]\n");
            System.exit(1);
        }
        try {
long temps = System.currentTimeMillis();
            File fileInput = new File(args[0]);
            FileInputStream fileInputHandle = new FileInputStream(fileInput);
            LzmaInputStream inputHandle = new LzmaInputStream(fileInputHandle);

            OutputStream outputHandle;
            if (args.length > 1) {
                outputHandle = Files.newOutputStream(new File(args[1]).toPath());
            } else {
                outputHandle = new ByteArrayOutputStream();
            }

            byte[] buffer = new byte[1 << 14];

            int ret = inputHandle.read(buffer);
            while (ret >= 1) {
                outputHandle.write(buffer, 0, ret);
                ret = inputHandle.read(buffer);
            }

            inputHandle.close();
            outputHandle.close();

            System.out.println("time : " + (System.currentTimeMillis() - temps) + " ms");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
