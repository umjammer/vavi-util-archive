/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipFile;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * ZIP �A�[�J�C�u����������T�[�r�X�v���o�C�_�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class ZipArchive implements Archive {

    /** */
    private ZipFile archive;

    /** */
    public ZipArchive(File file) throws IOException {
        this.archive = new ZipFile(file);
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
        Entry[] entries = new Entry[archive.size()];
        Enumeration<?> e = archive.entries();
        for (int i = 0; e.hasMoreElements(); i++) {
            entries[i] = new ZipEntry((java.util.zip.ZipEntry) e.nextElement());
        }
        return entries;
    }

    /**
     * �w�肳�ꂽ���O�� ZIP �t�@�C���G���g����Ԃ��܂��B
     * ������Ȃ��ꍇ�� null ��Ԃ��܂��B
     */
    public Entry getEntry(String name) {
        return new ZipEntry(archive.getEntry(name));
    }

    /**
     * �w�肳�ꂽ �t�@�C���G���g���̓��e��ǂݍ��ނ��߂̓��̓X�g���[����
     * �Ԃ��܂��B
     */
    public InputStream getInputStream(Entry entry) throws IOException {
        return archive.getInputStream(
            (java.util.zip.ZipEntry) entry.getWrappedObject());
    }

    /**
     * �t�@�C���̃p�X����Ԃ��܂��B
     */
    public String getName() {
        return archive.getName();
    }

    /**
     * �t�@�C�����̃G���g���̐���Ԃ��܂��B
     */
    public int size() {
        return archive.size();
    }
}

/* */
