/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * SevenZip アーカイブを処理するサービスプロバイダです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 030228 nsano initial version <br>
 */
public class SevenZipArchiveSpi implements ArchiveSpi {

    /** */
    private Object target;

    /**
     * 解凍できるかどうか調べます．
     * @param target 今のところ File しか受け付けません
     */
    public boolean canExtractInput(Object target) throws IOException {

        if (!(target instanceof File)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        this.target = target;

        InputStream is =
            new BufferedInputStream(new FileInputStream((File) target));

        byte[] b = new byte[8];

        is.mark(8);
        int l = 0;
        while (l < 8) {
            l += is.read(b, l, 8 - l);
        }
        is.reset();

        is.close();

Debug.println("\n" + StringUtil.getDump(b));
        return b[0] == '7' &&
               b[1] == 'z' &&
               b[2] == (byte) 0xbc &&
               b[3] == (byte) 0xaf &&
               b[4] == (byte) 0x27 &&
               b[5] == (byte) 0x1c &&
               b[6] == (byte) 0x00 &&
               b[7] == (byte) 0x02;
    }

    /** TODO プロパティで選択可能に？ */
    public Archive createArchiveInstance() throws IOException {
        return new NativeSevenZipArchive((File) target);
    }
}

/* */
