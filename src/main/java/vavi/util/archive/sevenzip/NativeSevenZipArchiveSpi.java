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
public class NativeSevenZipArchiveSpi implements ArchiveSpi {

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

        byte[] b = new byte[8];

        is.mark(8);
        int l = 0;
        while (l < 8) {
            l += is.read(b, l, 8 - l);
        }
        is.reset();

        is.close();

Debug.println("\n" + StringUtil.getDump(b));
        return b[0] == '7' &&
               b[1] == 'z' &&
               b[2] == (byte) 0xbc &&
               b[3] == (byte) 0xaf &&
               b[4] == (byte) 0x27 &&
               b[5] == (byte) 0x1c &&
               b[6] == (byte) 0x00 &&
               b[7] == (byte) 0x02;
    }

    @Override
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new NativeSevenZipArchive((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"7z", "7Z"};
    }
}

/* */
