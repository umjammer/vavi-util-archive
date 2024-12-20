/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * The service provider for LHA archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 */
public class LhaArchiveSpi implements ArchiveSpi {

    /**
     * @param target {@link File} and {@link InputStream} are supported.
     */
    @Override
    public boolean canExtractInput(Object target) throws IOException {
        if (!isSupported(target)) {
            return false;
        }

        InputStream is = null;
        boolean needToClose = false;

        if (target instanceof File) {
            is = new BufferedInputStream(Files.newInputStream(((File) target).toPath()));
            needToClose = true;
        } else if (target instanceof InputStream) {
            is = (InputStream) target;
            if (!is.markSupported()) {
                throw new IllegalArgumentException("InputStream should support #mark()");
            }
        } else {
            assert false : target.getClass().getName();
        }

        byte[] b = new byte[5];

        is.mark(7);
        is.skip(2);
        int l = 0;
        while (l < 5) {
            l += is.read(b, l, 5 - l);
        }
        is.reset();

        if (needToClose) {
            is.close();
        }

        return b[0] == '-' &&
               b[1] == 'l' &&
               b[2] == 'h' &&
               b[4] == '-';
    }

    /**
     * @param obj {@link File} and {@link InputStream} are supported.
     * @throws IllegalArgumentException unsupported type is specified to <code>obj</code>.
     */
    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        if (obj instanceof File) {
            return new LhaArchive((File) obj);
        } else if (obj instanceof InputStream) {
            return new LhaArchive((InputStream) obj);
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class, InputStream.class};
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"lha", "LHA"};
    }
}
