/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.binhex;

import java.io.IOException;
import java.io.InputStream;

import org.gjt.convert.binhex.BinHex4InputStream;

import vavi.util.archive.spi.InputStreamSpi;


/**
 * BinHex アーカイブを処理するサービスプロバイダです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 */
public class BinhexInputStreamSpi implements InputStreamSpi {

    /** */
    private Object target;

    /** */
    private static final String COMMENT =
        "(This file must be converted with BinHex 4.0).";

    /**
     * 解凍できるかどうか調べます．
     * @param target 今のところ {#link InputStream} しか受け付けません
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

        byte[] b = new byte[47];

        is.mark(47);
        int l = 0;
        while (l < 47) {
            l += is.read(b, l, 47 - l);
        }
        is.reset();

        return COMMENT.equals(new String(b, "ISO-8859-1"));
    }

    /** */
    public InputStream createInputStreamInstance()
        throws IOException {

        return new BinHex4InputStream((InputStream) target);
    }
}

/* */
