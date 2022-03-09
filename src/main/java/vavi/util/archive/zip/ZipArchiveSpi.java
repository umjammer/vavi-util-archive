/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * The ZIP SPI using the JDK zip library.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 *          0.01 030128 nsano implements <br>
 */
public class ZipArchiveSpi implements ArchiveSpi {

    @Override
    public boolean canExtractInput(Object target) throws IOException {

        InputStream is;
        boolean needToClose = false;

        if (File.class.isInstance(target)) {
            is = new BufferedInputStream(new FileInputStream(File.class.cast(target)));
            needToClose = true;
        } else if (InputStream.class.isInstance(target)) {
            is = InputStream.class.cast(target);
            if (!is.markSupported()) {
                throw new IllegalArgumentException("InputStream should support #mark()");
            }
        } else {
            throw new IllegalArgumentException("not supported type " + target.getClass().getName());
        }

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
    public Archive createArchiveInstance(Object obj) throws IOException {
        if (File.class.isInstance(obj)) {
            return new ZipArchive(File.class.cast(obj));
        } else if (InputStream.class.isInstance(obj)) {
            return new ZipArchive(InputStream.class.cast(obj));
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }
}

/* */
