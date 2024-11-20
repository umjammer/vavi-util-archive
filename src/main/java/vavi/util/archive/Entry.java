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
     * Returns the CRC-32 checksum of the uncompressed entry data.
     */
    long getCrc();

    /**
     * Returns the extra field data for the entry.
     */
    Object getExtra();

    /**
     * Returns the compression method of the entry.
     */
    int getMethod();

    /**
     * Returns the name of the entry.
     */
    String getName();

    /**
     * Returns the uncompressed size of the entry data.
     */
    long getSize();

    /**
     * Returns the last modification time of the entry.
     */
    long getTime();

    /**
     * Returns true if this is a directory entry.
     */
    boolean isDirectory();

    /**
     * Sets the optional comment string for the entry.
     */
    void setComment(String comment);

    /**
     * Sets the size of the compressed entry data.
     */
    void setCompressedSize(long csize);

    /**
     * Sets the CRC-32 checksum of the uncompressed entry data.
     */
    void setCrc(long crc);

    /**
     * Sets the optional extra field data for the entry.
     */
    void setExtra(Object extra);

    /**
     * Sets the compression method for the entry.
     */
    void setMethod(int method);

    /**
     * Sets the uncompressed size of the entry data.
     */
    void setSize(long size);

    /**
     * Sets the last modification time of the entry.
     */
    void setTime(long time);
}
