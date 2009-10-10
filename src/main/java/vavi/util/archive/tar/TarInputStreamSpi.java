/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.tar;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.spi.InputStreamSpi;


/**
 * Tar アーカイブを処理するサービスプロバイダです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 */
public class TarInputStreamSpi implements InputStreamSpi {

    /** */
    private Object target;

    /** */
    private static final int SKIP = 257;

    /**
     * 解凍できるかどうか調べます．
     * @param target 今のところ {#link InputStream} しか受け付けません
     */
    public boolean canExpandInput(Object target) throws IOException {

        if (!(target instanceof InputStream)) {
            throw new IllegalArgumentException("not supported type " + target.getClass());
        }

        this.target = target;

        InputStream is = (InputStream) target;

        if (!is.markSupported()) {
Debug.println(is);
            throw new IllegalArgumentException("cannot mark to stream");
        }

        byte[] t = new byte[SKIP];
        byte[] b = new byte[5];

        is.mark(SKIP + 5);
        int l = 0;
        while (l < SKIP) {
            int r = is.read(t, l, SKIP - l);
            if (r == -1) {
                throw new EOFException("no header 1");
            }
            l += r;
        }
//System.err.println(StringUtil.getDump(t));
        l = 0;
        while (l < 5) {
            int r = is.read(b, l, 5 - l);
            if (r == -1) {
                throw new EOFException("no magic");
            }
            l += r;
        }
        is.reset();

Debug.println("tar magic:\n" + StringUtil.getDump(b));
        return "ustar".equals(new String(b, "ISO-8859-1")) ||
            (b[0] == 0x00 &&    // TODO magic 無い奴がいる
             b[1] == 0x00 &&
             b[2] == 0x00 &&
             b[3] == 0x00 &&
             b[4] == 0x00 &&
             isAllAsciiAndNull(t));
    }

    /** */
    private boolean isAllAsciiAndNull(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] < 0x20 || bytes[i] > 0x7e) && bytes[i] != 0x00) {
                return false;
            }
        }
        return true;
    }

    /** */
    public InputStream createInputStreamInstance()
        throws IOException {

        return new TarArchiveInputStream((InputStream) target);
    }
}

/* */
