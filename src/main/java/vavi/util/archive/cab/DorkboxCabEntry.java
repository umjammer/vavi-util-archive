/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;


import vavi.util.archive.Entry;

import dorkbox.cabParser.structure.CabEntry;
import dorkbox.cabParser.structure.CabFileEntry;
import dorkbox.cabParser.structure.CabFolderEntry;


/**
 * DorkboxCabEntry．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120216 nsano initial version <br>
 */
public class DorkboxCabEntry implements Entry<CabEntry> {

    /** */
    private CabEntry entry;

    /** */
    public DorkboxCabEntry(CabEntry entry) {
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
        return -1;
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを返します。
     */
    public long getCrc() {
        return -1;
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
        return isDirectory() ? CabFolderEntry.class.cast(entry).getCompressionMethod() : -1;
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
        return isDirectory() ? -1 : CabFileEntry.class.cast(entry).getSize();
    }

    /**
     * エントリの修正時間を返します。
     */
    public long getTime() {
        return isDirectory() ? -1 : CabFileEntry.class.cast(entry).getDate().getTime();
    }

    /**
     * これがディレクトリエントリである場合に、true を返します。
     */
    public boolean isDirectory() {
        return CabFolderEntry.class.isInstance(entry);
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
    public CabEntry getWrappedObject() {
        return entry;
    }
}

/* */
