/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import vavi.util.archive.Archive;


/**
 * The SPI for 7zip using Apache commons compress.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 */
public class ApacheSevenZipArchiveSpi extends SevenZipArchiveSpi {

    /**
     * @param target currently accept {@link File} only.
     */
    @Override
    public boolean canExtractInput(Object target) throws IOException {
        if (!isSupported(target)) {
            return false;
        }

        InputStream is = new BufferedInputStream(Files.newInputStream(((File) target).toPath()));

        return super.canExtractInput(is, true);
    }

    @Override
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new ApacheSevenZipArchive((File) obj);
    }
}

/* */
