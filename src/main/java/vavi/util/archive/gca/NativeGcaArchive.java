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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.spi.CommonEntry;
import vavi.util.win32.DateUtil;


/**
 * UnGCA32.dll �̃��b�p�[�N���X�ł��B
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 030226 nsano initial version <br>
 *          0.01 030228 nsano implements archive <br>
 */
public class NativeGcaArchive implements Archive {

    /** */
    private List<CommonEntry> entries = new ArrayList<CommonEntry>();

    /** */
    private File file;

    /** */
    public NativeGcaArchive(File file) throws IOException {

        this.file = file;

        openArchive(file.getPath(), 0);

        if (findFirst("*.*")) {
            do {
                CommonEntry entry = new CommonEntry();

                entry.setSize(getCurrentOriginalSize());
                entry.setCompressedSize(getCurrentCompressedSize());
                entry.setCrc(getCurrentCRC());
                entry.setMethod(getCurrentMethod());
                entry.setName(currentFilename);
                entry.setTime(DateUtil.dosDateTimeToLong(getCurrentDate(),
                                                         getCurrentTime()));
                entries.add(entry);
System.err.println(StringUtil.paramString(entry));
System.err.println("time: " + new Date(entry.getTime()));
            } while (findNext());
        }
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

    /** */
    private static final String commandLineBase = "e \"{0}\" \"{1}\" \"{2}\"";
    
    /**
     * �w�肳�ꂽ �t�@�C���G���g���̓��e��ǂݍ��ނ��߂̓��̓X�g���[����
     * �Ԃ��܂��B
     * TODO password, keyfile
     */
    public InputStream getInputStream(Entry entry) throws IOException {

        String commandLine = MessageFormat.format(commandLineBase,
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

    // for Native method ------------------------------------------------------

    /** �C���X�^���X���ʎq */
    @SuppressWarnings("unused")
    private int instance;

    /** getFileName �����܂������񂩂��p */
    private String currentFilename;

    // �C���^�[�t�F�[�X

    /** �R�}���h�������^���āC�e��̏��ɑ�����s���܂��B */
    private native void exec(String command) throws IOException;
    /** �o�[�W������Ԃ��܂��B */
    private native int getVersion();
    /** ���쒆���ۂ��𓾂܂��B */
    @SuppressWarnings("unused")
    private native boolean isRunning();

    /** �w��t�@�C�������ɂƂ��Đ��������ǂ�����Ԃ��܂��B */
    @SuppressWarnings("unused")
    private native boolean checkArchive(String filename, int mode);
    /** �w�肳�ꂽ���Ƀt�@�C���Ɋi�[����Ă���t�@�C�����𓾂܂��B */
    @SuppressWarnings("unused")
    private native int getFileCount(String filename) throws IOException;
    /** ���Ƀt�@�C�����J���܂��B */
    private native void openArchive(String filename, int mode) throws IOException;
    /** ���Ƀt�@�C������܂��B */
    private native void closeArchive() throws IOException;
    /** �ŏ��̊i�[�t�@�C���̏��𓾂܂��B */
    private native boolean findFirst(String key) throws IOException;
    /** 2 �Ԗڈȍ~�̊i�[�t�@�C���̏��𓾂܂��B */
    private native boolean findNext();

    /** �i�[�t�@�C���̃t�@�C�����𓾂܂��B */
    @SuppressWarnings("unused")
    private native String getCurrentFileName() throws IOException;
    /** �i�[�t�@�C���̈��k�@�𓾂܂��B */
    private native int getCurrentMethod() throws IOException;
    /** �i�[�t�@�C���̃T�C�Y�𓾂܂��B */
    private native long getCurrentOriginalSize() throws IOException;
    /** �i�[�t�@�C���̈��k�T�C�Y�𓾂܂��B */
    private native long getCurrentCompressedSize() throws IOException;
    /** �i�[�t�@�C���̓��t�� DOS �`���œ��܂��B */
    private native int getCurrentDate() throws IOException;
    /** �i�[�t�@�C���̎����� DOS �`���œ��܂��B */
    private native int getCurrentTime() throws IOException;
    /** �i�[�t�@�C���̃`�F�b�N�T���𓾂܂��B */
    private native long getCurrentCRC() throws IOException;

    /** �����Ƀ}�b�`�����t�@�C���̃T�C�Y�̍��v�𓾂܂��B */
    @SuppressWarnings("unused")
    private native long getSelectedSize() throws IOException;
    /** �����Ƀ}�b�`�����t�@�C���̈��k�T�C�Y�̍��v�𓾂܂��B */
    @SuppressWarnings("unused")
    private native long getSelectedCompressedSize() throws IOException;
    /** �����Ƀ}�b�`�����t�@�C���̑S�̂̈��k���𓾂܂��B */
    @SuppressWarnings("unused")
    private native int getSelectedRatio() throws IOException;

    /** */
    static {
        System.loadLibrary("GcaWrapper");
    }

    // for Native method ------------------------------------------------------

    /** */
    public static void main(String[] args) throws IOException {
        NativeGcaArchive gca = new NativeGcaArchive(new File(args[0]));
System.err.println("gca: " + gca.getVersion());
//System.err.println("handle: " + gca.instance);
//System.err.println("size: " + gca.getSelectedSize());
//System.err.println("csize: " + gca.getSelectedCompressedSize());
//System.err.println("ratio: " + gca.getSelectedRatio());
        Entry entry = gca.getEntry(args[1]);
        InputStream is = gca.getInputStream(entry);
System.err.println("is: " + is);
        System.exit(0);
    }
}

/* */
