/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import vavi.util.archive.Archive;


/**
 * The SPI for RAR archive using the library 'vavi:java-unrar'.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 *          0.01 030128 nsano implements <br>
 *          0.02 030211 nsano use ComRarArchive <br>
 */
public class PureJavaRarArchiveSpi extends RarArchiveSpi {

    /**
     * @param target currently accept {@link File} only.
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
        } else {
            assert false : target.getClass().getName();
        }

        return canExtractInput(is, needToClose);
    }

    @Override
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new PureJavaRarArchive((File) obj);
    }
}

/* */
