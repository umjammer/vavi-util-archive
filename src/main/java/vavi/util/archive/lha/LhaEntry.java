/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import vavi.util.archive.WrappedEntry;

import jp.gr.java_conf.dangan.util.lha.CompressMethod;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;


/**
 * LhaEntry represents a LHA archive entry.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 030211 nsano implements #isDirectory() <br>
 *          0.02 030211 nsano jLha 0.06 compatible <br>
 */
public class LhaEntry implements WrappedEntry<LhaHeader> {

    /** */
    private LhaHeader entry;

    /** */
    public LhaEntry(LhaHeader header) {
        this.entry = header;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public long getCompressedSize() {
        return entry.getCompressedSize();
    }

    @Override
    public long getCrc() {
        return entry.getCRC();
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
        return entry.getPath();
    }

    @Override
    public long getSize() {
        return entry.getOriginalSize();
    }

    @Override
    public long getTime() {
        return entry.getLastModified().getTime();
    }

    @Override
    public boolean isDirectory() {
        return CompressMethod.LHD.equals(entry.getCompressMethod());
    }

    @Override
    public void setComment(String comment) {
        // TODO
    }

    @Override
    public void setCompressedSize(long csize) {
        entry.setCompressedSize(csize);
    }

    @Override
    public void setCrc(long crc) {
        entry.setCRC((int) crc);
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
        entry.setOriginalSize(size);
    }

    @Override
    public void setTime(long time) {
        // TODO
    }

    @Override
    public Object clone() {
        return entry.clone();
    }

    @Override
    public LhaHeader getWrappedObject() {
        return entry;
    }
}
