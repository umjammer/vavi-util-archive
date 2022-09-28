/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

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

        return canExtractInput(is, needToClose);
    }

    /**
     * @param obj {@link File} and {@link InputStream} are supported.
     * @throws IllegalArgumentException unsupported type is specified to <code>obj</code>.
     */
    public Archive createArchiveInstance(Object obj) throws IOException {
        if (obj instanceof File) {
            return new DorkboxCabArchive((File) obj);
        } else if (obj instanceof InputStream) {
            return new DorkboxCabArchive((InputStream) obj);
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }
}

/* */
