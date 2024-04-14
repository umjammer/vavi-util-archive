/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;

import vavi.util.archive.WrappedEntry;


/**
 * The wrapper for a 7zip archive entry.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021104 nsano initial version <br>
 */
public class ApacheSevenZipEntry implements WrappedEntry<SevenZArchiveEntry> {

    /** */
    private SevenZArchiveEntry entry;

    /** */
    public ApacheSevenZipEntry(SevenZArchiveEntry entry) {
        this.entry = entry;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public long getCompressedSize() {
        return -1;
    }

    @Override
    public long getCrc() {
        return entry.getCrcValue();
    }

    @Override
    public Object getExtra() {
        return null;
    }

    @Override
    public int getMethod() {
        return -1;
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
        return entry.getCreationDate().getTime();
    }

    @Override
    public boolean isDirectory() {
        return entry.isDirectory();
    }

    @Override
    public void setComment(String comment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCompressedSize(long csize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCrc(long crc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setExtra(Object extra) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMethod(int method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSize(long size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTime(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException();
    }

    /** */
    @Override
    public SevenZArchiveEntry getWrappedObject() {
        return entry;
    }
}
