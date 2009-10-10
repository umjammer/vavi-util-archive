/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.stuffit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.spi.CommonEntry;


/**
 * StuffItWrapper.dll �̃��b�p�[�N���X�ł��B
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 030226 nsano initial version <br>
 *          0.01 030228 nsano implements archive <br>
 */
@SuppressWarnings("unused")
public class NativeStuffItArchive implements Archive {

    /** */
    private List<CommonEntry> entries = new ArrayList<CommonEntry>();

    /** */
    private File file;

    /** */
    public NativeStuffItArchive(File file) throws IOException {
System.err.println("StuffIt.dll: " + getVersion());

        this.file = file;

        openArchive(file.getPath(), 0);
    }

    /**
     * �t�@�C������܂��B
     */
    public void close() throws IOException {
        closeArchive();
    }

    /**
     * �t�@�C���G���g���̗񋓂�Ԃ��܂��B
     */
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    /**
     * �w�肳�ꂽ���O�� ZIP �t�@�C���G���g����Ԃ��܂��B
     */
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * �w�肳�ꂽ �t�@�C���G���g���̓��e��ǂݍ��ނ��߂̓��̓X�g���[����
     * �Ԃ��܂��B
     */
    public InputStream getInputStream(Entry entry) throws IOException {

        String commandLine = MessageFormat.format("e \"{0}\" \"{1}\" \"{2}\"",
                                                  file.getPath(),
                                                  System.getProperty("java.io.tmpdir"),
                                                  entry.getName());
Debug.println("commandLine: " + commandLine);

        exec(commandLine);

        String temporaryFileName = System.getProperty("java.io.tmpdir") + entry.getName();
        File temporaryFile = new File(temporaryFileName);
        if (temporaryFile.exists()) {
            return new BufferedInputStream(new FileInputStream(temporaryFile));
        } else {
            throw new IOException("cannot extract: " + temporaryFileName);
        }
    }

    /**
     * �t�@�C���̃p�X����Ԃ��܂��B
     */
    public String getName() {
        return file.getPath();
    }

    /**
     * �t�@�C�����̃G���g���̐���Ԃ��܂��B
     */
    public int size() {
        return entries.size();
    }

    /** 
     * from JNI
     * @param filename TODO unicode
     * @param time TODO unused
     * @param size
     * @param compressedSize
     * @param crc TODO unused
     * @param method TODO unused
     */
    protected void addFileEntry(String filename, long time, int size, int compressedSize, int crc, int method) {
        CommonEntry entry = new CommonEntry();

        entry.setSize(size);
        entry.setCompressedSize(compressedSize);
        entry.setCrc(crc);
        entry.setMethod(method);
        entry.setName(filename);
        entry.setTime(time);

        entries.add(entry);
    }

    /**
     * from JNI
     * @param filename TODO unicode
     * @param time TODO unused
     */
    protected void addDirectoryEntry(String filename, long time) {
        CommonEntry entry = new CommonEntry();

        entry.setDirectory(true);
        entry.setName(filename);
        entry.setTime(time);

        entries.add(entry);
    }

    // for Native method ------------------------------------------------------

    /** �C���X�^���X���ʎq */
    private int instance;

    // �C���^�[�t�F�[�X

    /** �R�}���h�������^���āC�e��̏��ɑ�����s���܂��B */
    private native void exec(String command) throws IOException;
    /** �o�[�W������Ԃ��܂��B */
    private native int getVersion();

    /** ���Ƀt�@�C�����J���܂��B */
    private native void openArchive(String filename, int mode) throws IOException;
    /** ���Ƀt�@�C������܂��B */
    private native void closeArchive() throws IOException;

    /** */
    static {
        System.loadLibrary("StuffItWrapper");
    }

    // for Native method ------------------------------------------------------

    /** */
    public static void main(String[] args) throws IOException {
        NativeStuffItArchive sit = new NativeStuffItArchive(new File(args[0]));
System.err.println("sit: " + sit.getVersion());
//System.err.println("handle: " + sit.instance);
//System.err.println("size: " + sit.getSelectedSize());
//System.err.println("csize: " + sit.getSelectedCompressedSize());
//System.err.println("ratio: " + sit.getSelectedRatio());
//        Entry entry = sit.getEntry(args[1]);
//        InputStream is = sit.getInputStream(entry);
//System.err.println("is: " + is);
        for (Entry entry : sit.entries) {
            System.err.println("entry: " + entry);
        }
        System.exit(0);
    }
}

/* */
