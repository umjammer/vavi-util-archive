/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;


/**
 * アーカイブされた 1 エントリを表すクラスです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 */
public interface Entry extends Cloneable {

    /**
     * エントリのコメント文字列を返します。
     */
    String getComment();

    /**
     * 圧縮されたエントリデータのサイズを返します。
     */
    long getCompressedSize();

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを返します。
     */
    long getCrc();

    /**
     * エントリの補足フィールドデータを返します。
     */
    Object getExtra();

    /**
     * エントリの圧縮メソッドを返します。
     */
    int getMethod();

    /**
     * エントリの名前を返します。
     */
    String getName();

    /**
     * エントリデータの圧縮解除時のサイズを返します。
     */
    long getSize();

    /**
     * エントリの修正時間を返します。
     */
    long getTime();

    /**
     * これがディレクトリエントリである場合に、true を返します。
     */
    boolean isDirectory();

    /**
     * エントリに任意指定のコメント文字列を設定します。
     */
    void setComment(String comment);

    /**
     * 圧縮されたエントリデータのサイズを設定します。
     */
    void setCompressedSize(long csize);

    /**
     * 圧縮解除されたエントリデータの CRC-32 チェックサムを設定します。
     */
    void setCrc(long crc);

    /**
     * エントリに任意指定の補足フィールドデータを設定します。
     */
    void setExtra(Object extra);

    /**
     * エントリの圧縮メソッドを設定します。
     */
    void setMethod(int method);

    /**
     * エントリデータの圧縮解除時のサイズを設定します。
     */
    void setSize(long size);

    /**
     * エントリの修正時間を設定します。
     */
    void setTime(long time);

    /** TODO */
    Object getWrappedObject();
}

/* */
