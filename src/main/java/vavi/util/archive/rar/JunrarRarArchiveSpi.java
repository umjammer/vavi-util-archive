/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

import vavi.util.archive.Archive;


/**
 * The SPI for RAR archive using the library 'com.github.junrar:junrar'.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 220922 nsano initial version <br>
 */
public class JunrarRarArchiveSpi extends RarArchiveSpi {

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

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        return new JunrarRarArchive((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class, InputStream.class};
    }
}

/* */
