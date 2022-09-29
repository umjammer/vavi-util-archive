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
 * The SPI for RAR archive using a native library.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 *          0.01 030128 nsano implements <br>
 *          0.02 030211 nsano use ComRarArchive <br>
 */
public class NativeRarArchiveSpi extends RarArchiveSpi {

    /**
     * 解凍できるかどうか調べます．
     * @param target 今のところ File しか受け付けません
     */
    public boolean canExtractInput(Object target) throws IOException {
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

    /* */
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new NativeRarArchive((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }
}

/* */
