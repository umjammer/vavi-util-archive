/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.bzip2;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import vavi.util.Debug;
import vavi.util.archive.spi.InputStreamSpi;


/**
 * The service provider for BZip2 archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040105 nsano initial version <br>
 */
public class BZip2InputStreamSpi implements InputStreamSpi {

    /** */
    private Object target;

    /**
     * @param target currently accepts only InputStream
     */
    @Override
    public boolean canExpandInput(Object target) throws IOException {

        if (!(target instanceof InputStream is)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        this.target = target; // TODO weird

        if (!is.markSupported()) {
            throw new IllegalArgumentException("cannot mark to stream");
        }

        byte[] b = new byte[3];

        is.mark(3);
        int l = 0;
        while (l < 3) {
            l += is.read(b, l, 3 - l);
        }
        is.reset();

        return b[0] == (byte) 0x42 &&
               b[1] == (byte) 0x5a &&
               b[2] == (byte) 0x68;
    }

    @Override
    public InputStream createInputStreamInstance()
        throws IOException {
Debug.println(target);
        return new BZip2CompressorInputStream((InputStream) target);
    }
}

/* */
