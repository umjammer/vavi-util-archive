/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * LHA アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 */
public class LhaArchiveSpi implements ArchiveSpi {

    /**
     * 解凍できるかどうか調べます．
     * @param target {@link File} and {@link InputStream} are supported.
     */
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
     * @throw IllegalArgumentException unsupported type is specified to <code>obj</code>.
     */
    public Archive createArchiveInstance(Object obj) throws IOException {
        if (File.class.isInstance(obj)) {
            return new LhaArchive(File.class.cast(obj));
        } else if (InputStream.class.isInstance(obj)) {
            return new LhaArchive(InputStream.class.cast(obj));
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }
}

/* */
