/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.spi.ArchiveSpi;


/**
 * The service provider for a RAR archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 *          0.01 030128 nsano implements <br>
 *          0.02 030211 nsano use ComRarArchive <br>
 */
public abstract class RarArchiveSpi implements ArchiveSpi {

    /**
     * Checks magic (first 4 byte).
     *
     * @param is need to support mark
     */
    protected boolean canExtractInput(InputStream is, boolean needToClose) throws IOException {
        byte[] b = new byte[4];

        is.mark(4);
        int l = 0;
        while (l < 4) {
            l += is.read(b, l, 4 - l);
        }
        is.reset();

        if (needToClose) {
            is.close();
        }

        return b[0] == 'R' &&
               b[1] == 'a' &&
               b[2] == 'r' &&
               b[3] == '!';
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"rar", "RAR"};
    }
}
