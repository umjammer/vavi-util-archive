/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.Archive;


/**
 * CAB アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 041002 nsano initial version <br>
 */
public class DorkboxCabArchiveSpi extends CabArchiveSpi {

    /**
     * 解凍できるかどうか調べます．
     * @param target 今のところ File, InputStream しか受け付けません
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

        return canExtractInput(is, needToClose);
    }

    /**
     * @param obj {@link File} and {@link InputStream} are supported.
     * @throw IllegalArgumentException unsupported type is specified to <code>obj</code>.
     */
    public Archive createArchiveInstance(Object obj) throws IOException {
        if (File.class.isInstance(obj)) {
            return new DorkboxCabArchive(File.class.cast(obj));
        } else if (InputStream.class.isInstance(obj)) {
            return new DorkboxCabArchive(InputStream.class.cast(obj));
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }
}

/* */
