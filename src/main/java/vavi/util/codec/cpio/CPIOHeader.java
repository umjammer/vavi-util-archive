
package vavi.util.codec.cpio;

import java.io.IOException;
import java.io.InputStream;

import vavi.util.Debug;

import static vavi.util.codec.cpio.CPIOInputStream.skip;


/**
 * <pre>
 * Archive formats:
 *    Binary
 *       Obsolete binary header starts with octal o070707 binary (two bytes).
 *       If the bytes are swapped (o070707 -&gt; 71C7, swapped is C771 or o143561).
 *       Header length is 26 bytes, followed by the null terminated filename.
 *       All header fields are either shorts or long in native byte order. Thus,
 *       all fields must be byte swapped in the 'C771' case. The header is
 *       extended past the filename to the nearest 2-byte boundary.
 *
 *    OldAscii
 *       Original ascii header starts with ascii &quot;070707&quot; (six bytes).
 *       Header length is 76 bytes. All fields are octal string values.
 *       All fields are six ascii chars, except mtime and filesize, which
 *       are 11 characters. Apparently, the header is not padded to any
 *       boundary.
 *  Field Name   Length in Bytes Notes
 *  c_magic      6               must be &quot;070707&quot;
 *  c_dev        6
 *  c_ino        6
 *  c_mode       6               see below for value
 *  c_uid        6
 *  c_gid        6
 *  c_nlink      6
 *  c_rdev       6               only valid for chr and blk special files
 *  c_mtime      11
 *  c_namesize   6               count includes terminating NUL in pathname
 *  c_filesize   11              must be 0 for FIFOs and directories
 *    NewAscii
 *       New ascii header starts with ascii &quot;070701&quot; (six bytes).
 *       Header length is 110 bytes. All fields are eight character
 *       hex string values. Checksum field is left as zero. The header
 *       is extended past the filename to the nearest 4-byte boundary.
 *    NewAsciiCRC
 *       New ascii header starts with ascii &quot;070702&quot; (six bytes).
 *       Same as NewAscii, except includes the CRC checksum.
 *  Field Name   Length in Bytes Notes
 *  c_magic      6               &quot;070701&quot; for &quot;new&quot; portable format
 *  &quot;070702&quot; for CRC format
 *  c_ino        8
 *  c_mode       8
 *  c_uid        8
 *  c_gid        8
 *  c_nlink      8
 *  c_mtime      8
 *  c_filesize   8               must be 0 for FIFOs and directories
 *  c_maj        8
 *  c_min        8
 *  c_rmaj       8               only valid for chr and blk special files
 *  c_rmin       8               only valid for chr and blk special files
 *  c_namesize   8               count includes terminating NUL in pathname
 *  c_chksum     8               0 for &quot;new&quot; portable format; for CRC format
 * </pre>
 */
public class CPIOHeader {

    public static final int FMT_UNKNOWN = 0;
    public static final int FMT_BINARY = 1;
    public static final int FMT_BINSWAP = 2;
    public static final int FMT_ASCII = 3;
    public static final int FMT_ASCNEW = 4;
    public static final int FMT_ASCCRC = 5;

    public int format = FMT_UNKNOWN;
    public int hdrSize = 0;
    public byte[] hdrBytes = null;
    public byte[] nameBytes = null;
    public int dev_major = 0;
    public int dev_minor = 0;
    public int rdev_major = 0;
    public int rdev_minor = 0;
    public int checksum = 0;
    public int inode = 0;
    public int mode = 0;
    public int uid = 0;
    public int gid = 0;
    public int nlink = 0;
    public int rdev = 0;
    public int mtime = 0;
    public int namesize = 0;
    public int filesize = 0;

    public String filename = null;

    public CPIOHeader() {
    }

    public CPIOHeader(InputStream in) throws IOException {
        this.readHeader(in);
    }

    public int getFilePadding() {
        int result = switch (this.format) {
            case CPIOHeader.FMT_ASCII -> 0;
            case CPIOHeader.FMT_ASCNEW, CPIOHeader.FMT_ASCCRC -> (4 - (this.filesize % 4)) % 4;
            case CPIOHeader.FMT_BINARY, CPIOHeader.FMT_BINSWAP -> (this.filesize % 2);
            default -> 0;
        };

        Debug.println("FILEPAD: size=" + this.filesize + "  pad=" + result);
        return result;
    }

    public int getNamePadding() {
        int result = 0;
        int size = this.namesize + this.hdrSize;

        result = switch (this.format) {
            case CPIOHeader.FMT_ASCII -> 0;
            case CPIOHeader.FMT_ASCNEW, CPIOHeader.FMT_ASCCRC -> (4 - (size % 4)) % 4;
            case CPIOHeader.FMT_BINARY, CPIOHeader.FMT_BINSWAP -> (size % 2);
            default -> result;
        };

Debug.println("NAMEPAD: size=" + size + "(nm=" + this.namesize + ")  pad=" + result);
        return result;
    }

    public void readHeader(InputStream in) throws IOException {
        int numRead;

        byte[] prime = new byte[6];

        numRead = in.read(prime);
        if (numRead < 6) {
            throw new IOException("unexpected eof reading header type");
        }

        String pStr = new String(prime);

        if (prime[0] == 0x30 && prime[1] == 0x37) {
            // Some version of the ASCII format
            if (prime[5] == 0x37) {
                // Old ASCII format
                this.hdrSize = 76;
                this.format = FMT_ASCII;
            } else if (prime[5] == 0x31) {
                // New ASCII format
                this.hdrSize = 110;
                this.format = FMT_ASCNEW;
            } else if (prime[5] == 0x32) {
                // New ASCII format w/ CRC
                this.hdrSize = 110;
                this.format = FMT_ASCCRC;
            } else {
                throw new IOException("invalid header type[1] '" + pStr + "'");
            }
        }
        // else if ( prime[0] == 0x71 && prime[1] == 0xC7 )
        else if (prime[0] == 113 && prime[1] == -57) {
            // Binary Big Endian (no byte swap) (network byte order)
            this.hdrSize = 26;
            this.format = FMT_BINARY;
        }
        // else if ( prime[0] == 0xC7 && prime[1] == 0x71 )
        else if (prime[0] == -57 && prime[1] == 113) {
            // Binary Little Endian (Intel byte swap) (! network byte order)
            this.hdrSize = 26;
            this.format = FMT_BINSWAP;
        } else {
            throw new IOException("invalid header type[2] '" + pStr + "' " + prime[0] + ", " + prime[1] + ", " + prime[2] + ", " + prime[3] + ", " + prime[4] + ", " + prime[5]);
        }

        this.hdrBytes = new byte[this.hdrSize];
        System.arraycopy(prime, 0, this.hdrBytes, 0, 6);
        numRead = in.read(this.hdrBytes, 6, (this.hdrSize - 6));
Debug.println("FORMAT:  " + this.format);
Debug.println("HDRSIZE: " + this.hdrSize);

        if ((numRead + 6) != this.hdrBytes.length) {
            throw new IOException("unexpected eof reading header bytes");
        }

        this.parseHeader(this.hdrBytes);

Debug.println("NMSIZE:  " + this.namesize);

        this.nameBytes = new byte[this.namesize];

        numRead = in.read(this.nameBytes);

        if (numRead < this.namesize) {
            throw new IOException("unexpected eof reading filename bytes");
        }

        this.filename = new String(this.nameBytes, 0, this.namesize - 1);

        skip(in, this.getNamePadding());
    }

    public void parseHeader(byte[] hdr) throws IOException {
        if (this.format == FMT_BINARY || this.format == FMT_BINSWAP) {
            this.parseBinaryHeader(hdr);
        } else if (this.format == FMT_ASCII) {
            this.parseOldAsciiHeader(hdr);
        } else if (this.format == FMT_ASCNEW || this.format == FMT_ASCCRC) {
            this.parseNewAsciiHeader(hdr);
        }
    }

    public void parseBinaryHeader(byte[] hdr) {
        int offset = 2;

        @SuppressWarnings("unused")
        int device = readShort(hdr, offset, "device");
        offset += 2;

        this.inode = readShort(hdr, offset, "inode");
        offset += 2;

        this.mode = readShort(hdr, offset, "mode");
        offset += 2;

        this.uid = readShort(hdr, offset, "uid");
        offset += 2;

        this.gid = readShort(hdr, offset, "gid");
        offset += 2;

        this.nlink = readShort(hdr, offset, "nlink");
        offset += 2;

        @SuppressWarnings("unused")
        int rdev = readShort(hdr, offset, "rdev");
        offset += 2;

        this.mtime = readInt(hdr, offset, "mtime");
        offset += 4;

Debug.println("MTIME: " + this.mtime);
        this.namesize = readShort(hdr, offset, "namesize");
        offset += 2;

Debug.println("NAMESIZE: " + this.namesize);
        this.filesize = readInt(hdr, offset, "filesize");
Debug.println("FILESIZE: " + this.filesize);
    }

    public void parseNewAsciiHeader(byte[] hdr) throws IOException {
        int offset = 6;

        this.inode = readHex(hdr, offset, 8, "inode");
        offset += 8;

        this.mode = readHex(hdr, offset, 8, "mode");
        offset += 8;

        this.uid = readHex(hdr, offset, 8, "uid");
        offset += 8;

        this.gid = readHex(hdr, offset, 8, "gid");
        offset += 8;

        this.nlink = readHex(hdr, offset, 8, "nlink");
        offset += 8;

        this.mtime = readHex(hdr, offset, 8, "mtime");
        offset += 8;

        this.filesize = readHex(hdr, offset, 8, "filesize");
        offset += 8;

        this.dev_major = readHex(hdr, offset, 8, "dev_major");
        offset += 8;
        this.dev_minor = readHex(hdr, offset, 8, "dev_minor");
        offset += 8;

        this.rdev_major = readHex(hdr, offset, 8, "rdev_major");
        offset += 8;
        this.rdev_minor = readHex(hdr, offset, 8, "rdev_minor");
        offset += 8;

        this.namesize = readHex(hdr, offset, 8, "namesize");
        offset += 8;
        this.checksum = readHex(hdr, offset, 8, "checksum");

        Debug.println("NAMESIZE: " + this.namesize);
Debug.println("FILESIZE: " + this.filesize);
Debug.println("MTIME:    " + this.mtime);
    }

    public void parseOldAsciiHeader(byte[] hdr) throws IOException {
        int offset = 6;

        @SuppressWarnings("unused")
        int device = readOctal(hdr, offset, 6, "mode");
        offset += 6;

        this.inode = readOctal(hdr, offset, 6, "inode");
        offset += 6;

        this.mode = readOctal(hdr, offset, 6, "mode");
        offset += 6;

        this.uid = readOctal(hdr, offset, 6, "uid");
        offset += 6;

        this.gid = readOctal(hdr, offset, 6, "gid");
        offset += 6;

        this.nlink = readOctal(hdr, offset, 6, "nlink");
        offset += 6;

        @SuppressWarnings("unused")
        int rdev = readOctal(hdr, offset, 6, "mode");
        offset += 6;

        this.mtime = readOctal(hdr, offset, 11, "mtime");
        offset += 11;

        this.namesize = readOctal(hdr, offset, 8, "namesize");
        offset += 6;

        this.filesize = readOctal(hdr, offset, 11, "filesize");

        Debug.println("NAMESIZE: " + this.namesize);
Debug.println("FILESIZE: " + this.filesize);
Debug.println("MTIME:    " + this.mtime);
    }

    private int readShort(byte[] buf, int offset, String field) {
        int result;

        int b1, b0;

        if (this.format == FMT_BINSWAP) {
            b0 = buf[offset + 0];
            b1 = buf[offset + 1];
        } else {
            b0 = buf[offset + 1];
            b1 = buf[offset + 0];
        }

Debug.printf("READSHORT: IN [1, 0] = [ %02X, %02X ]\n", b1, b0);
Debug.println("READSHORT: [0]'" + buf[offset + 0] + " [1]'" + buf[offset + 1] + "' b0=" + b0 + "  b1=" + b1);

        if (b0 < 0) {
            b0 = b0 + 256;
        }
        if (b1 < 0) {
            b1 = b1 + 256;
        }
Debug.println("READSHORT: ADJUSTED b0=" + b0 + "  b1=" + b1);

        result = (b1 << 8) + b0;

Debug.println("READSHORT: result = " + result + "  swap = " + (this.format == FMT_BINSWAP));

        return result;
    }

    private int readInt(byte[] buf, int offset, String field) {
        int result;

        int b3, b2, b1, b0;
Debug.printf("READINT: DATA [3, 2, 1, 0] = [ %02X, %02X, %02X, %02X ]\n", buf[offset + 0], buf[offset + 1], buf[offset + 2], buf[offset + 3]);
        if (this.format == FMT_BINSWAP) {
            b0 = buf[offset + 2];
            b1 = buf[offset + 3];
            b2 = buf[offset + 0];
            b3 = buf[offset + 1];
        } else {
            b0 = buf[offset + 3];
            b1 = buf[offset + 2];
            b2 = buf[offset + 1];
            b3 = buf[offset + 0];
        }

Debug.printf("READINT: READ [3, 2, 1, 0] = [ %02X, %02X, %02X, %02X ]\n", b3, b2, b1, b0);
        if (b0 < 0) {
            b0 = b0 + 256;
        }
        if (b1 < 0) {
            b1 = b1 + 256;
        }
        if (b2 < 0) {
            b2 = b2 + 256;
        }
        if (b3 < 0) {
            b3 = b3 + 256;
        }

Debug.println("READINT: IN [3, 2, 1, 0] = [ " + b3 + "," + b2 + "," + b1 + "," + b0 + " ]");

        result = (b3 << 24) + (b2 << 16) + (b1 << 8) + b0;

Debug.println("READINT: result = " + result);

        return result;
    }

    private int readOctal(byte[] buf, int offset, int length, String field) throws IOException {
        int result = 0;

        for (int i = 0; i < length; ++i) {
            byte ch = buf[offset + i];

            if (ch < (byte) '0' || ch > (byte) '7') {
                throw new IOException("invalid octal character '" + (char) ch + "' in field '" + field + "'");
            }

            result = (result * 8) + (ch - (byte) '0');
        }

        return result;
    }

    private int readHex(byte[] buf, int offset, int length, String field) throws IOException {
        int result = 0;

        for (int i = 0; i < length; ++i) {
            byte ch = buf[offset + i];

            if (!((ch >= (byte) '0' && ch <= (byte) '9') || (ch >= (byte) 'A' && ch <= (byte) 'F') || (ch >= (byte) 'a' && ch <= (byte) 'f'))) {
                throw new IOException("invalid hex character '" + (char) ch + "' in field '" + field + "'");
            }

            int dig;

            if (/*ch >= (byte) '0' &&*/ ch <= (byte) '9') {
                dig = ch - (byte) '0';
            } else if (/*ch >= (byte) 'A' &&*/ ch <= (byte) 'F') {
                dig = 10 + (ch - (byte) 'A');
            } else /*if (ch >= (byte) 'a' && ch <= (byte) 'f')*/ {
                dig = 10 + (ch - (byte) 'a');
            }

            result = (result * 16) + dig;
        }

Debug.println("PARSE HEX: '" + field + "'  '" + (new String(buf, offset, length)) + "' result = " + result);

        return result;
    }
}
