/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import vavi.util.archive.WrappedEntry;


/**
 * Wrapper for ZIP archived file entry.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021104 nsano initial version <br>
 */
public class AntZipEntry implements WrappedEntry<org.apache.tools.zip.ZipEntry> {

    /** */
    private org.apache.tools.zip.ZipEntry entry;

    /** */
    public AntZipEntry(org.apache.tools.zip.ZipEntry entry) {
        this.entry = entry;
    }

    @Override
    public String getComment() {
        return entry.getComment();
    }

    @Override
    public long getCompressedSize() {
        return entry.getCompressedSize();
    }

    @Override
    public long getCrc() {
        return entry.getCrc();
    }

    @Override
    public Object getExtra() {
        return entry.getExtra();
    }

    @Override
    public int getMethod() {
        return entry.getMethod();
    }

    @Override
    public String getName() {
        return entry.getName();
    }

    @Override
    public long getSize() {
        return entry.getSize();
    }

    @Override
    public long getTime() {
        return entry.getTime();
    }

    @Override
    public boolean isDirectory() {
        return entry.isDirectory();
    }

    @Override
    public void setComment(String comment) {
        entry.setComment(comment);
    }

    @Override
    public void setCompressedSize(long csize) {
        entry.setCompressedSize(csize);
    }

    @Override
    public void setCrc(long crc) {
        entry.setCrc(crc);
    }

    @Override
    public void setExtra(Object extra) {
        entry.setExtra((byte[]) extra);
    }

    @Override
    public void setMethod(int method) {
        entry.setMethod(method);
    }

    @Override
    public void setSize(long size) {
        entry.setSize(size);
    }

    @Override
    public void setTime(long time) {
        entry.setTime(time);
    }

    @Override
    public Object clone() {
        return entry.clone();
    }

    @Override
    public org.apache.tools.zip.ZipEntry getWrappedObject() {
        return entry;
    }
}
