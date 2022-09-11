/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.util.logging.Level;

import vavi.util.Debug;
import vavi.util.archive.WrappedEntry;


/**
 * Wrapper for ZIP archived file entry.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021104 nsano initial version <br>
 */
public class ZipEntry implements WrappedEntry<java.util.zip.ZipEntry> {

    /** */
    private java.util.zip.ZipEntry entry;

    /** @param entry TODO vavi-nio-filesystem-archive request to be null */
    public ZipEntry(java.util.zip.ZipEntry entry) {
if (entry == null) {
 Debug.println(Level.FINE, "*** entry is null ***");
 //new Exception("*** DUMMY ***").printStackTrace();
}
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
        return entry == null || entry.isDirectory();
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
    public java.util.zip.ZipEntry getWrappedObject() {
        return entry;
    }
}

/* */
