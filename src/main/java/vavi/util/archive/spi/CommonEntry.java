/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.spi;

import vavi.util.StringUtil;
import vavi.util.archive.Entry;


/**
 * 共通的なアーカイブエントリです．
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030211 nsano initial version <br>
 *          0.01 030227 nsano implements clone() <br>
 */
public class CommonEntry implements Entry {

    /** */
    private String comment;
    /** */
    private long compressedSize;
    /** */
    private long crc;
    /** */
    private Object extra;
    /** */
    private int method;
    /** */
    private String name;
    /** */
    private long size;
    /** */
    private long time;
    /** */
    private boolean directory;

    /** */
    public CommonEntry() {
    }

    /**
     * エントリのコメント文字列を返します。
     */
    public String getComment() {
        return comment;
    }

    /**
     * 圧縮されたエントリデータのサイズを返します。
     */
    public long getCompressedSize() {
        return compressedSize;
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを返します。
     */
    public long getCrc() {
        return crc;
    }

    /**
     * エントリの補足フィールドデータを返します。
     */
    public Object getExtra() {
        return extra;
    }

    /**
     * エントリの圧縮メソッドを返します。
     */
    public int getMethod() {
        return method;
    }

    /**
     * エントリの名前を返します。
     */
    public String getName() {
        return name;
    }

    /**
     * エントリの名前を設定します。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * エントリデータの圧縮解除時のサイズを返します。
     */
    public long getSize() {
        return size;
    }

    /**
     * エントリの修正時間を返します。
     */
    public long getTime() {
        return time;
    }

    /**
     * これがディレクトリエントリである場合に、true を返します。
     */
    public boolean isDirectory() {
        return directory;
    }

    /** */
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    /**
     * エントリに任意指定のコメント文字列を設定します。
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 圧縮されたエントリデータのサイズを設定します。
     */
    public void setCompressedSize(long csize) {
        this.compressedSize = csize;
    }

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを設定します。
     */
    public void setCrc(long crc) {
        this.crc = crc;
    }

    /**
     * エントリに任意指定の補足フィールドデータを設定します。
     * @param extra Cloneable でなくてはいけません
     */
    public void setExtra(Object extra) {
        this.extra = extra;
    }

    /**
     * エントリの圧縮メソッドを設定します。
     */
    public void setMethod(int method) {
        this.method = method;
    }

    /**
     * エントリデータの圧縮解除時のサイズを設定します。
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * エントリの修正時間を設定します。
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * このエントリのコピーを返します。
     * (deep copy, else extra(shallow copy))
     */
    public Object clone() {
        try {
            CommonEntry clone = (CommonEntry) Class.forName(getClass().getName()).newInstance();
            clone.comment = (comment == null) ? null : new String(comment);
            clone.compressedSize = compressedSize;
            clone.crc = crc;
            clone.method = method;
            clone.name = (name == null) ? null : new String(name);
            clone.size = size;
            clone.time = time;
            clone.directory = directory;
            return clone;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** */
    public Object getWrappedObject() {
        return this;
    }

    /** */
    public String toString() {
        return StringUtil.paramString(this);
    }
}

/* */
