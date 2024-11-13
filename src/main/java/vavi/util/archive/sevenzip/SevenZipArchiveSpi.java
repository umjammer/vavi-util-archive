/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import vavi.util.StringUtil;
import vavi.util.archive.spi.ArchiveSpi;

import static java.lang.System.getLogger;


/**
 * The base SPI for 7zip.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 */
public abstract class SevenZipArchiveSpi implements ArchiveSpi {

    private static final Logger logger = getLogger(SevenZipArchiveSpi.class.getName());

    /**
     *
     * @param is need to support mark
     */
    protected boolean canExtractInput(InputStream is, boolean needToClose) throws IOException {
        byte[] b = new byte[8];

        is.mark(8);
        int l = 0;
        while (l < 8) {
            l += is.read(b, l, 8 - l);
        }
        is.reset();

        if (needToClose) {
            is.close();
        }

logger.log(Level.DEBUG, "\n" + StringUtil.getDump(b));
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
    public String[] getFileSuffixes() {
        return new String[] {"7z", "7Z"};
    }
}
