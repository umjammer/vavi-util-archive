/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

import vavi.util.archive.Archive;


/**
 * The SPI for SevenZip using native library.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030228 nsano initial version <br>
 */
public class NativeSevenZipArchiveSpi extends SevenZipArchiveSpi {

    /**
     * @param target currently accept {@link File} only.
     */
    @Override
    public boolean canExtractInput(Object target) throws IOException {

        if (!isSupported(target)) {
            return false;
        }

        InputStream is;
        boolean needToClose;

        if (target instanceof File) {
            is = new BufferedInputStream(Files.newInputStream(((File) target).toPath()));
            needToClose = true;
        } else {
            throw new IllegalArgumentException("not supported type " + target.getClass().getName());
        }

        return canExtractInput(is, needToClose);
    }

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        return new NativeSevenZipArchive((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }
}

/* */
