/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.arj;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * ARJ �A�[�J�C�u����������T�[�r�X�v���o�C�_�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 041002 nsano initial version <br>
 */
public class ArjArchiveSpi implements ArchiveSpi {

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

        return b[0] == 'A' && // TODO
               b[1] == 'R' &&
               b[2] == 'J';
    }

    /** TODO �v���p�e�B�őI���\�ɁH */
    public Archive createArchiveInstance() throws IOException {
        return new ComArjArchive((File) target);
    }
}

/* */
