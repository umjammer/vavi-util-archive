/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.tar;

import java.util.Date;

import vavi.util.archive.WrappedEntry;


/**
 * Wrapper for the TAR archived file entry.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 */
public class TarEntry implements WrappedEntry<org.apache.commons.compress.archivers.tar.TarArchiveEntry> {

    /** */
    private org.apache.commons.compress.archivers.tar.TarArchiveEntry entry;

    /** */
    public TarEntry(org.apache.commons.compress.archivers.tar.TarArchiveEntry entry) {
        this.entry = entry;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public long getCompressedSize() {
        return entry.getSize();
    }

    @Override
    public long getCrc() {
        return 0;
    }

    @Override
    public Object getExtra() {
        return null;
    }

    @Override
    public int getMethod() {
        return 0;
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
        return entry.getModTime().getTime();
    }

    @Override
    public boolean isDirectory() {
        return entry.isDirectory();
    }

    @Override
    public void setComment(String comment) {
        // TODO
    }

    @Override
    public void setCompressedSize(long csize) {
        entry.setSize(csize);
    }

    @Override
    public void setCrc(long crc) {
        // TODO
    }

    @Override
    public void setExtra(Object extra) {
        // TODO
    }

    @Override
    public void setMethod(int method) {
        // TODO
    }

    @Override
    public void setSize(long size) {
        entry.setSize(size);
    }

    @Override
    public void setTime(long time) {
        entry.setModTime(new Date(time));
    }

    /** */
    public org.apache.commons.compress.archivers.tar.TarArchiveEntry getWrappedObject() {
        return entry;
    }
}

/* */
