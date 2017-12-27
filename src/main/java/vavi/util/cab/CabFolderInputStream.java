/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.cab;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import vavi.io.LittleEndianDataInputStream;


/**
 * An InputStream that decodes CFDATA blocks belonging to a CabFolder.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040929 nsano initial version <br>
 */
class CabFolderInputStream extends InputStream {
    /** */
    @SuppressWarnings("unused")
    private CabFolder folder;
    /** */
    private LittleEndianDataInputStream in;
    /** */
    private InputStream iis = null;
    /** */
    private byte[] compressed = new byte[128 * 1024];
    /** */
    private byte[] uncompressed = new byte[256 * 1024];

    /** */
    public CabFolderInputStream(CabFolder folder, InputStream is) {
        this.folder = folder;
        this.in = new LittleEndianDataInputStream(is);
    }

    /** */
    InputStream readBlock() throws IOException {

        int compressedBytes = in.readShort();
        int unCompressedBytes = in.readShort();
        byte[] reserved = new byte[0];
        if (reserved.length > 0) {
            in.readFully(reserved);
        }
        if (in.readByte() != 0x43) {
            throw new IOException("malformed block header");
        }
        if (in.readByte() != 0x4b) {
            throw new IOException("malformed block header");
        }

        in.readFully(compressed, 0, compressedBytes - 2);

        Inflater i = new Inflater(true);
        i.setInput(compressed, 0, compressedBytes - 2);

        if (unCompressedBytes > uncompressed.length) {
            uncompressed = new byte[unCompressedBytes];
        }
        try {
            i.inflate(uncompressed, 0, uncompressed.length);
        } catch (DataFormatException e) {
e.printStackTrace(System.err);
            throw (IOException) new IOException().initCause(e);
        }
        return new ByteArrayInputStream(uncompressed, 0, unCompressedBytes);
    }

    /** */
    public int available() throws IOException {
        return (iis == null) ? 0 : iis.available();
    }

    /** */
    public void close() throws IOException {
        iis.close();
    }

    /** */
    public void mark(int i) {
    }

    /** */
    public boolean markSupported() {
        return false;
    }

    /** */
    public void reset() {
    }

    /** */
    public long skip(long l) throws IOException {
        if (iis == null) {
            iis = readBlock();
        }

        int ret = 0;
        while (l > ret) {
            long numread = iis.skip(l - ret);
            if ((numread == 0) || (numread == -1)) {
                iis = readBlock();
            } else {
                ret += numread;
            }
        }
        return ret;
    }

    /** */
    public int read(byte[] b, int off, int len) throws IOException {
        if (iis == null) {
            iis = readBlock();
        }

        int ret = 0;
        while (len > ret) {
            int numread = iis.read(b, off + ret, len - ret);
            if ((numread == 0) || (numread == -1)) {
                iis = readBlock();
            } else {
                ret += numread;
            }
        }
        return ret;
    }

    /** */
    public int read() throws IOException {
        if (iis == null) {
            iis = readBlock();
        }

        int ret = iis.read();
        if (ret == -1) {
            iis = readBlock();
            ret = iis.read();
        }
        return ret;
    }
}

/* */
