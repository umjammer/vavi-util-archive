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

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


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

        if (!(target instanceof File)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        InputStream is =
            new BufferedInputStream(Files.newInputStream(((File) target).toPath()));

        return super.canExtractInput(is, true);
    }

    @Override
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new NativeSevenZipArchive((File) obj);
    }
}

/* */
