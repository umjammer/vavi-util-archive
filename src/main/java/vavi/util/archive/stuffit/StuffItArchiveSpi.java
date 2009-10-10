/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.stuffit;

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
 * StuffIt �A�[�J�C�u����������T�[�r�X�v���o�C�_�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060106 nsano initial version <br>
 */
public class StuffItArchiveSpi implements ArchiveSpi {

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

        byte[] b = new byte[8];

        is.mark(8);
        int l = 0;
        while (l < 8) {
            l += is.read(b, l, 8 - l);
        }
        is.reset();

Debug.println("\n" + StringUtil.getDump(b));
        return new String(b).equals("StuffIt ") ||
               new String(b).equals("SIT!rRau");
    }

    /** TODO �v���p�e�B�őI���\�ɁH */
    public Archive createArchiveInstance() throws IOException {
        return new NativeStuffItArchive((File) target);
    }
}

/* */
