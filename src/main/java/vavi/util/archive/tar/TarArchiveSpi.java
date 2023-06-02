/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.tar;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * The service provider for TAR archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 */
public class TarArchiveSpi implements ArchiveSpi {

    /** */
    private static final int SKIP = 257;

    /**
     * @param target currently accepts only InputStream
     */
    @Override
    public boolean canExtractInput(Object target) throws IOException {

        if (!(target instanceof InputStream)) {
            throw new IllegalArgumentException("not supported type " + target.getClass());
        }

        InputStream is = (InputStream) target;

        if (!is.markSupported()) {
Debug.println(is);
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
Debug.println(Level.FINER, StringUtil.getDump(t));
        l = 0;
        while (l < 5) {
            int r = is.read(b, l, 5 - l);
            if (r == -1) {
                throw new EOFException("no magic");
            }
            l += r;
        }
        is.reset();

Debug.println(Level.FINE, "tar magic:\n" + StringUtil.getDump(b));
        return "ustar".equals(new String(b, StandardCharsets.ISO_8859_1)) ||
            (b[0] == 0x00 && // TODO w/ magic
             b[1] == 0x00 &&
             b[2] == 0x00 &&
             b[3] == 0x00 &&
             b[4] == 0x00 &&
             isAllAsciiAndNull(t));
    }

    /** */
    private boolean isAllAsciiAndNull(byte[] bytes) {
        for (byte b : bytes) {
            if ((b < 0x20 || b > 0x7e) && b != 0x00) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        return new TarArchive((InputStream) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {InputStream.class};
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"tar", "TAR"};
    }
}

/* */
