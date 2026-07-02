/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.tar;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import vavi.util.StringUtil;
import vavi.util.archive.spi.InputStreamSpi;

import static java.lang.System.getLogger;


/**
 * The service provider for Tar archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 */
public class TarInputStreamSpi implements InputStreamSpi {

    private static final Logger logger = getLogger(TarInputStreamSpi.class.getName());

    /** */
    private Object target;

    /** */
    private static final int SKIP = 257;

    /** a tar archive consists of 512 bytes records */
    private static final int RECORD_SIZE = 512;

    /** header checksum field */
    private static final int CHKSUM_OFFSET = 148;

    /** header checksum field */
    private static final int CHKSUM_LENGTH = 8;

    /**
     * @param target currently accepts only {#link InputStream}
     */
    @Override
    public boolean canExpandInput(Object target) throws IOException {

        if (!(target instanceof InputStream is)) {
            throw new IllegalArgumentException("not supported type: " + target.getClass());
        }

        if (is.available() < RECORD_SIZE) {
            throw new IllegalArgumentException("not enough size: " + is.available());
        }

        this.target = target;

        if (!is.markSupported()) {
logger.log(Level.DEBUG, is);
            throw new IllegalArgumentException("cannot mark to stream");
        }

        byte[] header = new byte[RECORD_SIZE];

        is.mark(RECORD_SIZE);
        int l = 0;
        while (l < RECORD_SIZE) {
            int r = is.read(header, l, RECORD_SIZE - l);
            if (r == -1) {
                throw new EOFException("no header 1");
            }
            l += r;
        }
        is.reset();

logger.log(Level.TRACE, "tar magic:\n" + StringUtil.getDump(Arrays.copyOfRange(header, SKIP, SKIP + 5)));
        return "ustar".equals(new String(header, SKIP, 5, StandardCharsets.ISO_8859_1)) ||
            (header[SKIP] == 0x00 &&    // Some tar files don't have magic.
             header[SKIP + 1] == 0x00 &&
             header[SKIP + 2] == 0x00 &&
             header[SKIP + 3] == 0x00 &&
             header[SKIP + 4] == 0x00 &&
             isAllAsciiAndNull(header, SKIP) &&
             isCheckSumOk(header));
    }

    /** */
    private static boolean isAllAsciiAndNull(byte[] bytes, int length) {
        for (int i = 0; i < length; i++) {
            byte b = bytes[i];
            if ((b < 0x20 || b > 0x7e) && b != 0x00) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifies the header checksum so that plain data files whose first 262
     * bytes happen to look like an old style (magic less) tar header are not
     * misdetected (e.g. sega genesis gym music files). Both the unsigned sum
     * and the historical signed sum are accepted.
     */
    private static boolean isCheckSumOk(byte[] header) {
        long stored = parseOctal(header, CHKSUM_OFFSET, CHKSUM_LENGTH);
        if (stored < 0) {
            return false;
        }
        long unsignedSum = 0;
        long signedSum = 0;
        for (int i = 0; i < header.length; i++) {
            // the checksum field itself is counted as spaces
            byte b = CHKSUM_OFFSET <= i && i < CHKSUM_OFFSET + CHKSUM_LENGTH ? (byte) ' ' : header[i];
            unsignedSum += b & 0xff;
            signedSum += b;
        }
        return stored == unsignedSum || stored == signedSum;
    }

    /** @return -1 when the field contains no octal number */
    private static long parseOctal(byte[] bytes, int offset, int length) {
        long result = 0;
        boolean seen = false;
        for (int i = offset; i < offset + length; i++) {
            int c = bytes[i] & 0xff;
            if (c == 0 || c == ' ') {
                if (seen) {
                    break;
                }
                continue; // leading padding
            }
            if (c < '0' || c > '7') {
                return -1;
            }
            result = result * 8 + (c - '0');
            seen = true;
        }
        return seen ? result : -1;
    }

    @Override
    public InputStream createInputStreamInstance() {
        return new TarArchiveInputStream((InputStream) target);
    }
}
