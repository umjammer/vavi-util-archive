/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.apache;

import org.apache.commons.compress.archivers.ArchiveEntry;

import vavi.util.archive.WrappedEntry;


/**
 * ApacheEntry represents an archive entry for apache-commons.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/17 umjammer initial version <br>
 */
public class ApacheEntry implements WrappedEntry<ArchiveEntry> {

    private ArchiveEntry entry;

    public ApacheEntry(ArchiveEntry entry) {
        this.entry = entry;
    }

    @Override
    public String getComment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getCompressedSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getCrc() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getExtra() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getMethod() {
        // TODO Auto-generated method stub
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
        return entry.getLastModifiedDate().getTime();
    }

    @Override
    public boolean isDirectory() {
        return entry.isDirectory();
    }

    @Override
    public void setComment(String comment) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCompressedSize(long csize) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCrc(long crc) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setExtra(Object extra) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMethod(int method) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSize(long size) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTime(long time) {
        // TODO Auto-generated method stub

    }

    @Override
    public ArchiveEntry getWrappedObject() {
        return entry;
    }
}
