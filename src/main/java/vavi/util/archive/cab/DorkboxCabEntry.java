/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;


import vavi.util.archive.WrappedEntry;

import dorkbox.cabParser.structure.CabEntry;
import dorkbox.cabParser.structure.CabFileEntry;
import dorkbox.cabParser.structure.CabFolderEntry;


/**
 * DorkboxCabEntry represents a CAB archive entry using a library Dorkbox.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120216 nsano initial version <br>
 */
public class DorkboxCabEntry implements WrappedEntry<CabEntry> {

    /** */
    private final CabEntry entry;

    /** */
    public DorkboxCabEntry(CabEntry entry) {
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
        return -1;
    }

    @Override
    public Object getExtra() {
        return null;
    }

    @Override
    public int getMethod() {
        return isDirectory() ? ((CabFolderEntry) entry).getCompressionMethod() : -1;
    }

    @Override
    public String getName() {
        return entry.getName();
    }

    @Override
    public long getSize() {
        return isDirectory() ? -1 : entry.getSize();
    }

    @Override
    public long getTime() {
        return isDirectory() ? -1 : ((CabFileEntry) entry).getDate().getTime();
    }

    @Override
    public boolean isDirectory() {
        return entry instanceof CabFolderEntry;
    }

    @Override
    public void setComment(String comment) {
        // TODO
    }

    @Override
    public void setCompressedSize(long csize) {
    }

    @Override
    public void setCrc(long crc) {
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
    }

    @Override
    public void setTime(long time) {
        // TODO
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CabEntry getWrappedObject() {
        return entry;
    }
}
