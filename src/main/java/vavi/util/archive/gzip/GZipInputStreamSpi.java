/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.gzip;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import vavi.util.archive.spi.InputStreamSpi;


/**
 * GZip アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 */
public class GZipInputStreamSpi implements InputStreamSpi {

    /** */
    private Object target;

    /**
     * 伸張できるかどうか調べます．
     * @param target 今のところ InputStream しか受け付けません
     */
    public boolean canExpandInput(Object target) throws IOException {

        if (!(target instanceof InputStream)) {
            throw new IllegalArgumentException("not supported type " + target.getClass());
        }

        this.target = target;

        InputStream is = (InputStream) target;

        if (!is.markSupported()) {
            throw new IllegalArgumentException("cannot mark to stream");
        }

        byte[] b = new byte[2];

        is.mark(2);
        int l = 0;
        while (l < 2) {
            l += is.read(b, l, 2 - l);
        }
        is.reset();

        return b[0] == (byte) 0x1f &&
               b[1] == (byte) 0x8b;
    }

    /** */
    public InputStream createInputStreamInstance()
        throws IOException {

        return new GZIPInputStream((InputStream) target);
    }
}

/* */
