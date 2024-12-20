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

    /**
     * @param target currently accepts only {#link InputStream}
     */
    @Override
    public boolean canExpandInput(Object target) throws IOException {

        if (!(target instanceof InputStream is)) {
            throw new IllegalArgumentException("not supported type " + target.getClass());
        }

        this.target = target;

        if (!is.markSupported()) {
logger.log(Level.DEBUG, is);
            throw new IllegalArgumentException("cannot mark to stream");
        }

        byte[] t = new byte[SKIP];
        byte[] b = new byte[5];

        is.mark(SKIP + 5);
        int l = 0;
        while (l < SKIP) {
            int r = is.read(t, l, SKIP - l);
            if (r == -1) {
                throw new EOFException("no header 1");
            }
            l += r;
        }
//logger.log(Level.TRACE, StringUtil.getDump(t));
        l = 0;
        while (l < 5) {
            int r = is.read(b, l, 5 - l);
            if (r == -1) {
                throw new EOFException("no magic");
            }
            l += r;
        }
        is.reset();

logger.log(Level.TRACE, "tar magic:\n" + StringUtil.getDump(b));
        return "ustar".equals(new String(b, StandardCharsets.ISO_8859_1)) ||
            (b[0] == 0x00 &&    // TODO magic 無い奴がいる
             b[1] == 0x00 &&
             b[2] == 0x00 &&
             b[3] == 0x00 &&
             b[4] == 0x00 &&
             isAllAsciiAndNull(t));
    }

    /** */
    private static boolean isAllAsciiAndNull(byte[] bytes) {
        for (byte b : bytes) {
            if ((b < 0x20 || b > 0x7e) && b != 0x00) {
                return false;
            }
        }
        return true;
    }

    @Override
    public InputStream createInputStreamInstance() {
        return new TarArchiveInputStream((InputStream) target);
    }
}
