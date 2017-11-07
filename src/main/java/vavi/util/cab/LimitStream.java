/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.cab;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * LimitStream.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040929 nsano initial version <br>
 */
class LimitStream extends FilterInputStream {
    int limit;

    /** */
    public LimitStream(InputStream is, int limit) {
        super(is);
        this.limit = limit;
    }

    /** */
    public int read() throws IOException {
        if (limit == 0) {
            return -1;
        }

        int ret = super.read();
        if (ret != -1) {
            limit--;
        }
        return ret;
    }

    /** */
    public int read(byte[] b, int off, int len) throws IOException {
        if (len > limit) {
            len = limit;
        }
        if (limit == 0) {
            return -1;
        }

        int ret = super.read(b, off, len);
        limit -= ret;
        return ret;
    }
}

/* */
