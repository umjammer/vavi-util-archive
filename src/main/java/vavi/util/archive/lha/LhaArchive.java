/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * LHA �A�[�J�C�u����������T�[�r�X�v���o�C�_�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class LhaArchive implements Archive {

    /** */
    private LhaFile archive;

    /** */
    public LhaArchive(File file) throws IOException {
        this.archive = new LhaFile(file);
    }

    /**
     * �t�@�C������܂��B
     */
    public void close() throws IOException {
        archive.close();
    }

    /**
     * �t�@�C���G���g���̗񋓂�Ԃ��܂��B
     */
    public Entry[] entries() {
        LhaHeader[] headers = archive.getEntries();
        Entry[] entries = new Entry[headers.length];
        for (int i = 0; i < headers.length; i++) {
            entries[i] = new LhaEntry(headers[i]);
        }
        return entries;
    }

    /**
     * �w�肳�ꂽ���O�̃t�@�C���G���g����Ԃ��܂��B
     * ������Ȃ��ꍇ�� null ��Ԃ��܂��B
     */
    public Entry getEntry(String name) {
        LhaHeader[] headers = archive.getEntries();
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].getPath().equals(name)) {
                return new LhaEntry(headers[i]);
            }
        }
        return null;
    }

    /**
     * �w�肳�ꂽ �t�@�C���G���g���̓��e��ǂݍ��ނ��߂̓��̓X�g���[����
     * �Ԃ��܂��B
     */
    public InputStream getInputStream(Entry entry) throws IOException {
        return archive.getInputStream((LhaHeader) entry.getWrappedObject());
    }

    /**
     * �t�@�C���̃p�X����Ԃ��܂��B
     */
    public String getName() {
        return null;
    }

    /**
     * �t�@�C�����̃G���g���̐���Ԃ��܂��B
     */
    public int size() {
        return archive.size();
    }
}

/* */
