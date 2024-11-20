/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.binhex;

import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;

import org.gjt.convert.binhex.BinHex4InputStream;
import vavi.util.StringUtil;
import vavi.util.archive.spi.InputStreamSpi;

import static java.lang.System.getLogger;


/**
 * The service provider for BinHex archive.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 * @see "http://klomp.org/JBinHex/JBinHex.html"
 */
public class BinhexInputStreamSpi implements InputStreamSpi {

    private static final Logger logger = getLogger(BinhexInputStreamSpi.class.getName());

    /** */
    private Object target;

    /** signature */
    private static final String COMMENT = "(This file must be converted with BinHex 4.0)";
    private static final int L = COMMENT.length();

    /**
     * @param target currently accepts {#link InputStream} only
     */
    @Override
    public boolean canExpandInput(Object target) throws IOException {

        if (!(target instanceof InputStream is)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        this.target = target;

        if (!is.markSupported()) {
            throw new IllegalArgumentException("cannot mark to stream");
        }

        byte[] b = new byte[L];

        is.mark(L);
        int l = 0;
        while (l < L) {
            l += is.read(b, l, L - l);
        }
        is.reset();

logger.log(Level.TRACE, "[" + new String(b, StandardCharsets.ISO_8859_1) + "], " + COMMENT.equals(new String(b, StandardCharsets.ISO_8859_1)) + "\n\n" + StringUtil.getDump(b));
        return COMMENT.equals(new String(b, StandardCharsets.ISO_8859_1));
    }

    @Override
    public InputStream createInputStreamInstance() {
        return new BinHex4InputStream((InputStream) target);
    }
}
