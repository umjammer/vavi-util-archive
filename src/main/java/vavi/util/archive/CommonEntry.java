/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import vavi.util.StringUtil;


/**
 * Common archive entry.
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

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public long getCompressedSize() {
        return compressedSize;
    }

    @Override
    public long getCrc() {
        return crc;
    }

    @Override
    public Object getExtra() {
        return extra;
    }

    @Override
    public int getMethod() {
        return method;
    }

    @Override
    public String getName() {
        return name;
    }

    /** Sets the name of this entry. */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public boolean isDirectory() {
        return directory;
    }

    /** Sets the directory of this entry. */
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setCompressedSize(long csize) {
        this.compressedSize = csize;
    }

    @Override
    public void setCrc(long crc) {
        this.crc = crc;
    }

    @Override
    public void setExtra(Object extra) {
        this.extra = extra;
    }

    @Override
    public void setMethod(int method) {
        this.method = method;
    }

    @Override
    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Returns copy of this entry.
     * (deep copy, else extra(shallow copy))
     */
    @Override
    public Object clone() {
        try {
            CommonEntry clone = (CommonEntry) Class.forName(getClass().getName()).getDeclaredConstructor().newInstance();
            clone.comment = (comment == null) ? null : comment;
            clone.compressedSize = compressedSize;
            clone.crc = crc;
            clone.method = method;
            clone.name = (name == null) ? null : name;
            clone.size = size;
            clone.time = time;
            clone.directory = directory;
            return clone;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return StringUtil.paramString(this);
    }
}

/* */
