/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * RAR アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 *          0.01 030128 nsano implements <br>
 *          0.02 030211 nsano use ComRarArchive <br>
 */
public abstract class RarArchiveSpi implements ArchiveSpi {

    /** */
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

    /** */
    public abstract Archive createArchiveInstance(Object obj) throws IOException;

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"rar", "RAR"};
    }
}

/* */
