
package vavi.util.codec.cpio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CPIOInputStream {

    private boolean debug = false;

    private InputStream in = null;

    private int entrySize = 0;

    private int entryOffset = 0;

    private CPIOEntry currEntry = null;

    public CPIOInputStream(InputStream in) {
        this.in = in;
    }

    public CPIOEntry getNextEntry() throws IOException {
        // Check to see if we need to skip over unread content
        // and padding bytes...
        if (this.currEntry != null) {
            int numToSkip = this.entrySize - this.entryOffset;

            if (this.debug) {
                System.err.println("CPIOStream: SKIP currENTRY '" + this.currEntry.getHeader().filename + "' SZ " + this.entrySize + " OFF " + this.entryOffset + "  skipping " + numToSkip + " bytes");
            }

            if (numToSkip > 0) {
                this.in.skip(numToSkip);
            }

            int padToSkip = this.currEntry.getHeader().getFilePadding();

            if (this.debug) {
                System.err.println("CPIOStream: SKIP PAD " + padToSkip + " bytes");
            }

            this.in.skip(padToSkip);
        }

        this.currEntry = new CPIOEntry(this.in);

        if (this.debug) {
            System.err.println("CPIOStream: SET CURRENTRY '" + this.currEntry.getHeader().filename + "' size = " + this.currEntry.getHeader().filesize);
        }

        this.entryOffset = 0;
        this.entrySize = this.currEntry.getHeader().filesize;

        return this.currEntry;
    }

    public int read(byte[] buf, int offset, int num) throws IOException {
        if (this.entryOffset >= this.entrySize)
            return -1;

        int numToRead = num;
        if (numToRead + this.entryOffset > this.entrySize) {
            numToRead = this.entrySize - this.entryOffset;
        }

        int numRead = this.in.read(buf, offset, numToRead);

        if (numRead > 0) {
            this.entryOffset += numRead;
        }

        return numRead;
    }

    public void copyEntryContents(OutputStream out) throws IOException {
        byte[] buf = new byte[32 * 1024];

        while (true) {
            int numRead = this.read(buf, 0, buf.length);
            if (numRead == -1)
                break;
            out.write(buf, 0, numRead);
        }
    }
}
