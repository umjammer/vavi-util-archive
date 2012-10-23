/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import jp.gr.java_conf.dangan.util.lha.CompressMethod;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;

import vavi.util.archive.Entry;


/**
 * LHA 圧縮のサービスプロバイダです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 030211 nsano implements #isDirectory() <br>
 *          0.02 030211 nsano jLha 0.06 compatible <br>
 */
public class LhaEntry implements Entry {

    /** */
    private LhaHeader entry;

    /** */
    public LhaEntry(LhaHeader header) {
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
        return entry.getCompressedSize();
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを返します。
     */
    public long getCrc() {
        return entry.getCRC();
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
        return entry.getPath();
    }

    /**
     * エントリデータの圧縮解除時のサイズを返します。
     */
    public long getSize() {
        return entry.getOriginalSize();
    }

    /**
     * エントリの修正時間を返します。
     */
    public long getTime() {
        return entry.getLastModified().getTime();
    }

    /**
     * これがディレクトリエントリである場合に、true を返します。
     */
    public boolean isDirectory() {
        return CompressMethod.LHD.equals(entry.getCompressMethod());
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
        entry.setCompressedSize(csize);
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを設定します。
     */
    public void setCrc(long crc) {
        entry.setCRC((int) crc);
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
        entry.setOriginalSize(size);
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
        return entry.clone();
    }

    /** */
    public Object getWrappedObject() {
        return entry;
    }
}

/* */
