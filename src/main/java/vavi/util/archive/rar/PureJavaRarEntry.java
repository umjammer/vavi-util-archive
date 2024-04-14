/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;


import java.io.File;

import vavi.util.archive.WrappedEntry;

import de.innosystec.unrar.rarfile.FileHeader;


/**
 * PureJavaRarEntry represents a RAR archived file
 * warring the library 'vavi:java-unrar'
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120216 nsano initial version <br>
 */
public class PureJavaRarEntry implements WrappedEntry<FileHeader> {

    /** */
    private FileHeader entry;

    /** */
    public PureJavaRarEntry(FileHeader header) {
        this.entry = header;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public long getCompressedSize() {
        return entry.getFullPackSize();
    }

    @Override
    public long getCrc() {
        return entry.getFileCRC();
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
        return entry.getFileName().replace('\\', File.separatorChar);
    }

    @Override
    public long getSize() {
        return entry.getDataSize();
    }

    @Override
    public long getTime() {
        return entry.getMTime().getTime();
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
    public FileHeader getWrappedObject() {
        return entry;
    }
}
