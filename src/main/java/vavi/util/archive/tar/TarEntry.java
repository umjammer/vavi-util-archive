/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.tar;

import java.util.Date;

import vavi.util.archive.Entry;


/**
 * TAR 圧縮のサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 */
public class TarEntry implements Entry {

    /** */
    private org.apache.commons.compress.archivers.tar.TarArchiveEntry entry;

    /** */
    public TarEntry(org.apache.commons.compress.archivers.tar.TarArchiveEntry entry) {
        this.entry = entry;
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
        return entry.getSize();
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを返します。
     */
    public long getCrc() {
        return 0;
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
        return entry.getModTime().getTime();
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
        entry.setSize(csize);
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを設定します。
     */
    public void setCrc(long crc) {
        // TODO
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
        entry.setSize(size);
    }

    /**
     * エントリの修正時間を設定します。
     */
    public void setTime(long time) {
        entry.setModTime(new Date(time));
    }

    /** */
    public Object getWrappedObject() {
        return entry;
    }
}

/* */
