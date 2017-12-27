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
import vavi.util.archive.spi.ArchiveSpi;


/**
 * CAB アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 041002 nsano initial version <br>
 */
public class CabArchiveSpi implements ArchiveSpi {

    /**
     * 解凍できるかどうか調べます．
     * @param target 今のところ File しか受け付けません
     */
    public boolean canExtractInput(Object target) throws IOException {

        if (!(target instanceof File)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        InputStream is =
            new BufferedInputStream(new FileInputStream((File) target));

        byte[] b = new byte[4];

        is.mark(4);
        int l = 0;
        while (l < 4) {
            l += is.read(b, l, 4 - l);
        }
        is.reset();

        is.close();

        return b[0] == 'M' &&
               b[1] == 'S' &&
               b[2] == 'C' &&
               b[3] == 'F';
    }

    /** TODO プロパティで選択可能に？ */
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new ComCabArchive((File) obj);
    }
}

/* */
