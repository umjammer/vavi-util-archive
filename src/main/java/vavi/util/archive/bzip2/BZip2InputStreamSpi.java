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
 * BZip2 アーカイブを処理するサービスプロバイダです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040105 nsano initial version <br>
 */
public class BZip2InputStreamSpi implements InputStreamSpi {

    /** */
    private Object target;

    /**
     * 伸張できるかどうか調べます．
     * @param target 今のところ InputStream しか受け付けません
     */
    public boolean canExpandInput(Object target) throws IOException {

        if (!(target instanceof InputStream)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        this.target = target;

        InputStream is = (InputStream) target;

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

    /** */
    public InputStream createInputStreamInstance()
        throws IOException {
Debug.println(target);
        return new BZip2CompressorInputStream((InputStream) target);
    }
}

/* */
