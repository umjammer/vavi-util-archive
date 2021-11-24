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
 * The service provider for BinHex archive.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 * @see "http://klomp.org/JBinHex/JBinHex.html"
 */
public class BinhexInputStreamSpi implements InputStreamSpi {

    /** */
    private Object target;

    /** signature */
    private static final String COMMENT =
        "(This file must be converted with BinHex 4.0).";

    /**
     * checks extractable or not.
     * @param target currently accepts {#link InputStream} only
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
