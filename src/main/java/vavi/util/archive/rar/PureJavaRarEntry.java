/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;


import de.innosystec.unrar.rarfile.FileHeader;

import vavi.util.archive.Entry;


/**
 * RAR 圧縮のサービスプロバイダです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 120216 nsano initial version <br>
 */
public class PureJavaRarEntry implements Entry {

    /** */
    private FileHeader entry;

    /** */
    public PureJavaRarEntry(FileHeader header) {
        this.entry = header;
    }

    /**
     * エントリのコメント文字列を返します。
     */
    public String getComment() {
        return null;
    }

    /**
     * 圧縮されたエントリデータのサイズを返します。
     */
    public long getCompressedSize() {
        return entry.getFullPackSize();
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを返します。
     */
    public long getCrc() {
        return entry.getFileCRC();
    }

    /**
     * エントリの補足フィールドデータを返します。
     */
    public Object getExtra() {
        return null;
    }

    /**
     * エントリの圧縮メソッドを返します。
     */
    public int getMethod() {
        return 0;
    }

    /**
     * エントリの名前を返します。
     */
    public String getName() {
        return entry.getFileNameString();
    }

    /**
     * エントリデータの圧縮解除時のサイズを返します。
     */
    public long getSize() {
        return entry.getDataSize();
    }

    /**
     * エントリの修正時間を返します。
     */
    public long getTime() {
        return entry.getArcTime().getTime();
    }

    /**
     * これがディレクトリエントリである場合に、true を返します。
     */
    public boolean isDirectory() {
        return entry.isDirectory();
    }

    /**
     * エントリに任意指定のコメント文字列を設定します。
     */
    public void setComment(String comment) {
        // TODO
    }

    /**
     * 圧縮されたエントリデータのサイズを設定します。
     */
    public void setCompressedSize(long csize) {
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを設定します。
     */
    public void setCrc(long crc) {
    }

    /**
     * エントリに任意指定の補足フィールドデータを設定します。
     */
    public void setExtra(Object extra) {
        // TODO
    }

    /**
     * エントリの圧縮メソッドを設定します。
     */
    public void setMethod(int method) {
        // TODO
    }

    /**
     * エントリデータの圧縮解除時のサイズを設定します。
     */
    public void setSize(long size) {
    }

    /**
     * エントリの修正時間を設定します。
     */
    public void setTime(long time) {
        // TODO
    }

    /**
     * このエントリのコピーを返します。
     */
    public Object clone() {
        throw new UnsupportedOperationException();
    }

    /** */
    public Object getWrappedObject() {
        return entry;
    }
}

/* */
