/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.stuffit;

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
 * The SPI for StuffIt archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060106 nsano initial version <br>
 */
public abstract class StuffItArchiveSpi implements ArchiveSpi {

    /**
     * @param target currently accepts {@link File} only
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
        return new String(b).equals("StuffIt ") ||
               new String(b).equals("SIT!rRau");
    }

    @Override
    public abstract Archive createArchiveInstance(Object obj) throws IOException;

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"sit", "SIT"};
    }
}

/* */
