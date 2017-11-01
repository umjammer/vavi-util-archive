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
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 */
public class LhaArchiveSpi implements ArchiveSpi {

    /** */
    private Object target;

    /**
     * 解凍できるかどうか調べます．
     */
    public boolean canExtractInput(Object target) throws IOException {

        if (!(target instanceof File)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        this.target = target;

        InputStream is =
            new BufferedInputStream(new FileInputStream((File) target));

        byte[] b = new byte[5];

        is.mark(7);
        is.skip(2);
        int l = 0;
        while (l < 5) {
            l += is.read(b, l, 5 - l);
        }
        is.reset();
        
        is.close();

        return b[0] == '-' &&
               b[1] == 'l' &&
               b[2] == 'h' &&
               b[4] == '-';
    }

    /** */
    public Archive createArchiveInstance() throws IOException {
        return new LhaArchive((File) target);
    }
}

/* */
