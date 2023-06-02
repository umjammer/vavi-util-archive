/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.spi.ArchiveSpi;


/**
 * The service provider for CAB archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 041002 nsano initial version <br>
 */
public abstract class CabArchiveSpi implements ArchiveSpi {

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

        return b[0] == 'M' &&
               b[1] == 'S' &&
               b[2] == 'C' &&
               b[3] == 'F';
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[] {"cab", "CAB"};
    }
}

/* */
