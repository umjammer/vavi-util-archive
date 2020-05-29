/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import vavi.util.archive.Entry;


/**
 * ZIP 圧縮のサービスプロバイダです．
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021104 nsano initial version <br>
 */
public class AntZipEntry implements Entry<org.apache.tools.zip.ZipEntry> {

    /** */
    private org.apache.tools.zip.ZipEntry entry;

    /** */
    public AntZipEntry(org.apache.tools.zip.ZipEntry entry) {
        this.entry = entry;
    }

    /**
     * エントリのコメント文字列を返します。
     */
    public String getComment() {
        return entry.getComment();
    }

    /**
     * 圧縮されたエントリデータのサイズを返します。
     */
    public long getCompressedSize() {
        return entry.getCompressedSize();
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを返します。
     */
    public long getCrc() {
        return entry.getCrc();
    }

    /**
     * エントリの補足フィールドデータを返します。
     */
    public Object getExtra() {
        return entry.getExtra();
    }

    /**
     * エントリの圧縮メソッドを返します。
     */
    public int getMethod() {
        return entry.getMethod();
    }

    /**
     * エントリの名前を返します。
     */
    public String getName() {
        return entry.getName();
    }

    /**
     * エントリデータの圧縮解除時のサイズを返します。
     */
    public long getSize() {
        return entry.getSize();
    }

    /**
     * エントリの修正時間を返します。
     */
    public long getTime() {
        return entry.getTime();
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
        entry.setComment(comment);
    }

    /**
     * 圧縮されたエントリデータのサイズを設定します。
     */
    public void setCompressedSize(long csize) {
        entry.setCompressedSize(csize);
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを設定します。
     */
    public void setCrc(long crc) {
        entry.setCrc(crc);
    }

    /**
     * エントリに任意指定の補足フィールドデータを設定します。
     */
    public void setExtra(Object extra) {
        entry.setExtra((byte[]) extra);
    }

    /**
     * エントリの圧縮メソッドを設定します。
     */
    public void setMethod(int method) {
        entry.setMethod(method);
    }

    /**
     * エントリデータの圧縮解除時のサイズを設定します。
     */
    public void setSize(long size) {
        entry.setSize(size);
    }

    /**
     * エントリの修正時間を設定します。
     */
    public void setTime(long time) {
        entry.setTime(time);
    }

    /**
     * このエントリのコピーを返します。
     */
    public Object clone() {
        return entry.clone();
    }

    /** */
    public org.apache.tools.zip.ZipEntry getWrappedObject() {
        return entry;
    }
}

/* */
