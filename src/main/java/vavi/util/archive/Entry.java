/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;


/**
 * Represents one entry in an archived data.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 */
public interface Entry extends Cloneable {

    /**
     * Returns the comment string for the entry.
     *
     * @return the comment string for the entry, or null if none
     * @see #setComment(String)
     */
    String getComment();

    /**
     * Returns the size of the compressed entry data.
     * <p>
     * In the case of a stored entry, the compressed size will be the same as the uncompressed size of the entry.
     * </p>
     * @return the size of the compressed entry data, or -1 if not known
     * @see #setCompressedSize(long)
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
}

/* */
