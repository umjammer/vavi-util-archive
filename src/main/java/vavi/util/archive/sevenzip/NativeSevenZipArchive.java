/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;
import vavi.util.archive.gca.NativeGcaArchive;
import vavi.util.win32.DateUtil;


/**
 * The wrapper class for 7-zip32.dll.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030228 nsano initial version <br>
 */
public class NativeSevenZipArchive implements Archive {

    /** */
    private List<CommonEntry> entries = new ArrayList<>();

    /** */
    private File file;

    /** */
    public NativeSevenZipArchive(File file) throws IOException {
Debug.println("7-zip32.dll: " + getVersion());

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
Debug.println(StringUtil.paramString(entry));
Debug.println("time: " + new Date(entry.getTime()));
            } while (findNext());
        }
    }

    @Override
    public void close() throws IOException {
        closeArchive();
    }

    @Override
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    @Override
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
  Debug.printf("%1$d, 0x%1$04x\n", code);
  Debug.println(rb.getString(errorCodeTable.getProperty(String.format("0x%04X", code))));
 } catch (Exception e2) {
  Debug.printStackTrace(e2);
 }
 throw e;
}

        String temporaryFileName = temporaryDirectoryString + File.separator + entry.getName();
        File temporaryFile = new File(temporaryFileName);
        if (temporaryFile.exists()) {
            return new BufferedInputStream(Files.newInputStream(temporaryFile.toPath()));
        } else {
            throw new IOException("cannpt extract: " + temporaryFileName);
        }
    }

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public int size() {
        return entries.size();
    }

    /** <codeText,localString> */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.util.archive.vavi.NativeSevenZipArchive", Locale.getDefault());

    /** <hexCode,codeText> */
    private static Properties errorCodeTable;

    /* */
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

    /** インスタンス識別子 */
    private int instance;

    /** getFileName がうまくいかんから代用 */
    private String currentFilename;

    // インターフェース

    /** コマンド文字列を与えて，各種の書庫操作を行います。 */
    private native void exec(String command) throws IOException;
    /** バージョンを返します。 */
    private native int getVersion();
    /** 動作中か否かを得ます。 */
    private native boolean isRunning();

    /** 指定ファイルが書庫として正しいかどうかを返します。 */
    private native boolean checkArchive(String filename, int mode);
    /** 指定された書庫ファイルに格納されているファイル数を得ます。 */
    private native int getFileCount(String filename) throws IOException;
    /** 書庫ファイルを開きます。 */
    private native void openArchive(String filename, int mode) throws IOException;
    /** 書庫ファイルを閉じます。 */
    private native void closeArchive() throws IOException;
    /** 最初の格納ファイルの情報を得ます。 */
    private native boolean findFirst(String key) throws IOException;
    /** 2 番目以降の格納ファイルの情報を得ます。 */
    private native boolean findNext();

    /** 格納ファイルのファイル名を得ます。 */
    private native String getCurrentFileName() throws IOException;
    /** 格納ファイルの圧縮法を得ます。 */
    private native int getCurrentMethod() throws IOException;
    /** 格納ファイルのサイズを得ます。 */
    private native long getCurrentOriginalSize() throws IOException;
    /** 格納ファイルの圧縮サイズを得ます。 */
    private native long getCurrentCompressedSize() throws IOException;
    /** 格納ファイルの日付を DOS 形式で得ます。 */
    private native int getCurrentDate() throws IOException;
    /** 格納ファイルの時刻を DOS 形式で得ます。 */
    private native int getCurrentTime() throws IOException;
    /** 格納ファイルのチェックサムを得ます。 */
    private native long getCurrentCRC() throws IOException;

    /** 検索にマッチしたファイルのサイズの合計を得ます。 */
    private native long getSelectedSize() throws IOException;
    /** 検索にマッチしたファイルの圧縮サイズの合計を得ます。 */
    private native long getSelectedCompressedSize() throws IOException;
    /** 検索にマッチしたファイルの全体の圧縮率を得ます。 */
    private native int getSelectedRatio() throws IOException;

    /* */
    static {
        System.loadLibrary("SevenZipWrapper");
    }
}

/* */
