/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.gca;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * GCA �A�[�J�C�u����������T�[�r�X�v���o�C�_�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 030128 nsano initial version <br>
 *          0.01 030128 nsano implements <br>
 */
public class GcaArchiveSpi implements ArchiveSpi {

    /** */
    private Object target;

    /**
     * �𓀂ł��邩�ǂ������ׂ܂��D
     * @param target ���̂Ƃ��� File �����󂯕t���܂���
     */
    public boolean canExtractInput(Object target) throws IOException {

        if (!(target instanceof File)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        this.target = target;

        InputStream is =
            new BufferedInputStream(new FileInputStream((File) target));

        byte[] b = new byte[4];

        is.mark(4);
        int l = 0;
        while (l < 4) {
            l += is.read(b, l, 4 - l);
        }
        is.reset();

        return b[0] == 'G' &&
               b[1] == 'C' &&
               b[2] == 'A';
    }

    /** TODO �v���p�e�B�őI���\�ɁH */
    public Archive createArchiveInstance() throws IOException {
        return new NativeGcaArchive((File) target);
    }
}

/* */
