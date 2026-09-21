/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.m3u;

import io.lindstrom.m3u8.model.ByteRange;
import io.lindstrom.m3u8.model.MediaSegment;
import vavi.util.archive.WrappedEntry;


/**
 * M3uEntry.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2026-09-22 nsano initial version <br>
 */
public class M3uEntry implements WrappedEntry<MediaSegment> {

    private final MediaSegment entry;

    private String comment;
    private long compressedSize = -1;
    private long crc = -1;
    private Object extra;
    private int method = 0;
    private String name;
    private long size = -1;
    private long time = -1;

    public M3uEntry(MediaSegment entry) {
        this.entry = entry;
    }

    @Override
    public MediaSegment getWrappedObject() {
        return entry;
    }

    @Override
    public String getComment() {
        if (comment != null) {
            return comment;
        }
        return entry.title().orElse(null);
    }

    @Override
    public long getCompressedSize() {
        if (compressedSize != -1) {
            return compressedSize;
        }
        return getSize();
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
        if (name != null) {
            return name;
        }
        return entry.uri();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getSize() {
        if (size != -1) {
            return size;
        }
        return entry.byteRange().map(ByteRange::length).orElse(-1L);
    }

    @Override
    public long getTime() {
        if (time != -1) {
            return time;
        }
        return entry.programDateTime().map(t -> t.toInstant().toEpochMilli()).orElse(-1L);
    }

    @Override
    public boolean isDirectory() {
        return getName().endsWith("/");
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

    @Override
    public Object clone() {
        throw new UnsupportedOperationException();
    }
}
