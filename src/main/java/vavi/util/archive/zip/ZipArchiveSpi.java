/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.spi.ArchiveSpi;


/**
 * The service provider for ZIP archive base.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 220929 nsano initial version <br>
 */
public abstract class ZipArchiveSpi implements ArchiveSpi {

    /**
     *
     * @param is need to support mark
     */
    protected boolean canExtractInput(InputStream is, boolean needToClose) throws IOException {

        byte[] b = new byte[2];

        is.mark(2);
        int l = 0;
        while (l < 2) {
            l += is.read(b, l, 2 - l);
        }
        is.reset();

        if (needToClose) {
            is.close();
        }

        return b[0] == 'P' &&
               b[1] == 'K';
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"zip", "ZIP", "cbz", "jar", "JAR", "epub"};
    }
}

/* */
