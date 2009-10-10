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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.gca.NativeGcaArchive;
import vavi.util.archive.spi.CommonEntry;
import vavi.util.win32.DateUtil;


/**
 * 7-zip32.dll �̃��b�p�[�N���X�ł��B
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 030228 nsano initial version <br>
 */
public class NativeSevenZipArchive implements Archive {

    /** */
    private List<CommonEntry> entries = new ArrayList<CommonEntry>();

    /** */
    private File file;

    /** */
    public NativeSevenZipArchive(File file) throws IOException {
System.err.println("7-zip32.dll: " + getVersion());

        this.file = file;

        openArchive(file.getPath(), 0);

        if (findFirst("*")) {
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

    /**
     * �w�肳�ꂽ �t�@�C���G���g���̓��e��ǂݍ��ނ��߂̓��̓X�g���[����
     * �Ԃ��܂��B
     */
    public InputStream getInputStream(Entry entry) throws IOException {

        File temporaryDirectory = new File(System.getProperty("java.io.tmpdir"));
        String temporaryDirectoryString = temporaryDirectory.getAbsolutePath();
Debug.println("temporaryDirectory: " + temporaryDirectoryString);

    	String commandLine = MessageFormat.format("x -hide -y \"{0}\" -o\"{1}\" \"{2}\"",
                                                  file.getPath(),
                                                  temporaryDirectoryString,
                                                  entry.getName());
Debug.println("commandLine: " + commandLine);

try {
        exec(commandLine);
} catch (IOException e) {
 try {
  int code = Integer.parseInt(e.getMessage());
  Debug.println(code + ", 0x" + StringUtil.toHex4(code));
  Debug.println(rb.getString(errorCodeTable.getProperty("0x" + StringUtil.toHex4(code))));
 } catch (Exception e2) {
  Debug.printStackTrace(e2);
 }
 throw e;
}

    	String temporaryFileName = temporaryDirectoryString + File.separator + entry.getName();
    	File temporaryFile = new File(temporaryFileName);
    	if (temporaryFile.exists()) {
    	    return new BufferedInputStream(new FileInputStream(temporaryFile));
    	} else {
    	    throw new IOException("cannpt extract: " + temporaryFileName);
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

    /** <codeText,localString> */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.util.archive.vavi.NativeSevenZipArchive", Locale.getDefault());

    /** <hexCode,codeText> */
    private static Properties errorCodeTable;

    /** */
    static {
        String path = "NativeSevenZipArchive_errorCode.properties";
        try {
            errorCodeTable = new Properties();
            errorCodeTable.load(NativeGcaArchive.class.getResourceAsStream(path));
        } catch (Exception e) {
Debug.printStackTrace(e);            
        }
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
        System.loadLibrary("SevenZipWrapper");
    }

    // for Native method ------------------------------------------------------

    /** java NativeSevenZipArchive sevenZipFile oneOfEntry */
    public static void main(String[] args) throws IOException {
        NativeSevenZipArchive sevenZip = new NativeSevenZipArchive(new File(args[0]));
        Entry entry = sevenZip.getEntry(args[1]);
        InputStream is = sevenZip.getInputStream(entry);
System.err.println("is: " + is);
//System.err.println("sevenZip: " + sevenZip.getVersion());
//System.err.println("handle: " + sevenZip.instance);
//System.err.println("size: " + sevenZip.getSelectedSize());
//System.err.println("csize: " + sevenZip.getSelectedCompressedSize());
//System.err.println("ratio: " + sevenZip.getSelectedRatio());
        System.exit(0);
    }
}

/* */
