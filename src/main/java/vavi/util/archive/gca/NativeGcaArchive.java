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
 * UnGCA32.dll のラッパークラスです。
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
     * ファイルを閉じます。
     */
    public void close() throws IOException {
        closeArchive();
    }

    /**
     * ファイルエントリの列挙を返します。
     */
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    /**
     * 指定された名前の ZIP ファイルエントリを返します。
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
     * 指定された ファイルエントリの内容を読み込むための入力ストリームを
     * 返します。
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
     * ファイルのパス名を返します。
     */
    public String getName() {
        return file.getPath();
    }

    /**
     * ファイル中のエントリの数を返します。
     */
    public int size() {
        return entries.size();
    }

    // for Native method ------------------------------------------------------

    /** インスタンス識別子 */
    @SuppressWarnings("unused")
    private int instance;

    /** getFileName がうまくいかんから代用 */
    private String currentFilename;

    // インターフェース

    /** コマンド文字列を与えて，各種の書庫操作を行います。 */
    private native void exec(String command) throws IOException;
    /** バージョンを返します。 */
    private native int getVersion();
    /** 動作中か否かを得ます。 */
    @SuppressWarnings("unused")
    private native boolean isRunning();

    /** 指定ファイルが書庫として正しいかどうかを返します。 */
    @SuppressWarnings("unused")
    private native boolean checkArchive(String filename, int mode);
    /** 指定された書庫ファイルに格納されているファイル数を得ます。 */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    private native long getSelectedSize() throws IOException;
    /** 検索にマッチしたファイルの圧縮サイズの合計を得ます。 */
    @SuppressWarnings("unused")
    private native long getSelectedCompressedSize() throws IOException;
    /** 検索にマッチしたファイルの全体の圧縮率を得ます。 */
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
