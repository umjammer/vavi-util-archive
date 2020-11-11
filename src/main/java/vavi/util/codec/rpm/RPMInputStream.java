
package vavi.util.codec.rpm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import vavi.util.codec.cpio.CPIOEntry;
import vavi.util.codec.cpio.CPIOInputStream;


public class RPMInputStream extends Object {
    private boolean debug = false;

    private InputStream rpmin = null;

    private GZIPInputStream gzin = null;

    private CPIOInputStream cpio = null;

    @SuppressWarnings("unused")
    private String name = null;
    @SuppressWarnings("unused")
    private String version = null;
    @SuppressWarnings("unused")
    private String release = null;
    @SuppressWarnings("unused")
    private String arch = null;

    private RPMLead lead = new RPMLead();

    private RPMHeader sigHeader = new RPMHeader();

    private RPMIndexEntry[] sigEntries = new RPMIndexEntry[0];

    @SuppressWarnings("unused")
    private byte[] sigBuffer = new byte[0];

    private RPMHeader headerHeader = new RPMHeader();

    private RPMIndexEntry[] headerEntries = new RPMIndexEntry[0];

    public RPMInputStream(InputStream in, String name, String version, String release, String arch) throws IOException {
        this.rpmin = in;
        this.name = name;
        this.version = version;
        this.release = release;
        this.arch = arch;

        this.readRPMLead();

        this.sigHeader = this.readRPMHeader();

        this.sigEntries = this.readRPMHeaderIndex(this.sigHeader.numEntries);

        // this.readSignatureContent();
        this.readHeaderContent(this.sigEntries, this.sigHeader.headerSize);

        this.skipSignaturePad(this.sigHeader.headerSize);

        this.headerHeader = this.readRPMHeader();

        this.headerEntries = this.readRPMHeaderIndex(this.headerHeader.numEntries);

        this.readHeaderContent(this.headerEntries, this.headerHeader.headerSize);

        this.gzin = new GZIPInputStream(this.rpmin);

        this.cpio = new CPIOInputStream(this.gzin);
    }

    public RPMLead getRPMLead() {
        return this.lead;
    }

    public RPMHeader getSignatureHeader() {
        return this.sigHeader;
    }

    public RPMIndexEntry getSignatureEntry(int idx) {
        if (idx < 0 || idx > this.sigEntries.length)
            return null;
        return this.sigEntries[idx];
    }

    public RPMHeader getHeaderHeader() {
        return this.headerHeader;
    }

    public RPMIndexEntry getHeaderEntry(int idx) {
        if (idx < 0 || idx > this.headerEntries.length)
            return null;
        return this.headerEntries[idx];
    }

    private void readRPMLead() throws InvalidRPMFileException {
        int numRead = 0;

        try {
            this.lead = new RPMLead();

            numRead = this.rpmin.read(this.lead.magic);
            if (numRead < this.lead.magic.length)
                throw new InvalidRPMFileException("unexpected eof reading lead magic bytes");

            numRead = this.rpmin.read();
            if (numRead == -1)
                throw new InvalidRPMFileException("unexpected eof reading lead major byte");
            this.lead.major = (byte) numRead;

            numRead = this.rpmin.read();
            if (numRead == -1)
                throw new InvalidRPMFileException("unexpected eof reading lead minor byte");
            this.lead.minor = (byte) numRead;

            try {
                this.lead.type = this.readShortInt();
            } catch (InvalidRPMFileException ex) {
                throw new InvalidRPMFileException("while reading lead type, " + ex.getMessage());
            }

            try {
                this.lead.archNum = this.readShortInt();
            } catch (InvalidRPMFileException ex) {
                throw new InvalidRPMFileException("while reading lead archNum, " + ex.getMessage());
            }

            numRead = this.rpmin.read(this.lead.name);
            if (numRead < this.lead.name.length)
                throw new InvalidRPMFileException("unexpected eof reading lead name bytes");

            try {
                this.lead.osNum = this.readShortInt();
            } catch (InvalidRPMFileException ex) {
                throw new InvalidRPMFileException("while reading lead osNum, " + ex.getMessage());
            }

            try {
                this.lead.signatureType = this.readShortInt();
            } catch (InvalidRPMFileException ex) {
                throw new InvalidRPMFileException("while reading lead signatureType, " + ex.getMessage());
            }

            numRead = this.rpmin.read(this.lead.reserved);
            if (numRead < this.lead.reserved.length)
                throw new InvalidRPMFileException("unexpected eof reading lead reserved bytes");
        } catch (IOException ex) {
            throw new InvalidRPMFileException("IOException - " + ex.getMessage());
        }
    }

    private RPMHeader readRPMHeader() throws InvalidRPMFileException {
        int numRead = 0;
        RPMHeader result = new RPMHeader();

        try {
            numRead = this.rpmin.read(result.magic);
            if (numRead < result.magic.length)
                throw new InvalidRPMFileException("unexpected eof reading header magic bytes");

            numRead = this.rpmin.read();
            if (numRead == -1)
                throw new InvalidRPMFileException("unexpected eof reading header version byte");
            result.version = (byte) numRead;

            numRead = this.rpmin.read(result.reserved);
            if (numRead < result.reserved.length)
                throw new InvalidRPMFileException("unexpected eof reading header reserved bytes");

            try {
                result.numEntries = this.readLongInt();
            } catch (InvalidRPMFileException ex) {
                throw new InvalidRPMFileException("while reading header number of entries, " + ex.getMessage());
            }

            try {
                result.headerSize = this.readLongInt();
            } catch (InvalidRPMFileException ex) {
                throw new InvalidRPMFileException("while reading header size, " + ex.getMessage());
            }
        } catch (IOException ex) {
            throw new InvalidRPMFileException("IOException - " + ex.getMessage());
        }

        return result;
    }

    private RPMIndexEntry[] readRPMHeaderIndex(int entries) throws InvalidRPMFileException {
        RPMIndexEntry[] result = new RPMIndexEntry[entries];

        for (int idx = 0; idx < entries; ++idx) {
            result[idx] = this.readRPMIndexEntry();
        }

        return result;
    }

    private RPMIndexEntry readRPMIndexEntry() throws InvalidRPMFileException {
        @SuppressWarnings("unused")
        int numRead = 0;
        String item = "tag";
        RPMIndexEntry result = new RPMIndexEntry();

        try {
            result.tag = this.readLongInt();
            item = "type";
            result.type = this.readLongInt();
            item = "offset";
            result.offset = this.readLongInt();
            item = "count";
            result.count = this.readLongInt();
        } catch (InvalidRPMFileException ex) {
            throw new InvalidRPMFileException("while reading index entry " + item + ", " + ex.getMessage());
        }

        return result;
    }

    private void skipSignaturePad(int headerSize) throws InvalidRPMFileException {
        try {
            int skip = headerSize % 8;
            byte[] buf = new byte[skip];
            int numRead = this.rpmin.read(buf);
            if (numRead < skip)
                throw new IOException("unexpected eof");
        } catch (IOException ex) {
            throw new InvalidRPMFileException("IOException skipping signature end pad bytes - " + ex.getMessage());
        }
    }

    private void readHeaderContent(RPMIndexEntry[] entries, int contentSize) throws IOException {
        int offset = 0;
        int numRead = 0;
        int numToRead = 0;
        RPMIndexEntry entry;
        int num = entries.length;

        byte[] readBuf = new byte[16];

        for (int i = 0; i < num; ++i) {
            entry = entries[i];

            if (entry.offset > offset) {
                numToRead = entry.offset - offset;
                byte[] skipbuf = new byte[numToRead];
                numRead = this.rpmin.read(skipbuf);
                if (this.debug)
                    System.err.println("skipped " + numRead + " header content bytes.");
                offset += numRead;
            }

            List<Object> values = new ArrayList<>();

            for (int cnt = 0; cnt < entry.count; ++cnt) {
                switch (entry.type) {
                case RPMIndexEntry.RPMTYPE_CHAR:
                    numToRead = 1;
                    numRead = this.rpmin.read(readBuf, 0, numToRead);
                    if (numRead < numToRead)
                        break;

                    offset += numRead;
                    if (this.debug)
                        System.err.println("CHAR[" + cnt + "](" + numRead + ") = '" + readBuf[0] + "'");
                    values.add(new Character((char) readBuf[0]));
                    break;

                case RPMIndexEntry.RPMTYPE_INT8:
                    numToRead = 1;
                    numRead = this.rpmin.read(readBuf, 0, numToRead);
                    if (numRead < numToRead)
                        break;

                    offset += numRead;
                    if (this.debug)
                        System.err.println("INT8[" + cnt + "](" + numRead + ") = '" + readBuf[0] + "'");
                    int int8 = (readBuf[0] & 0x000000FF);
                    values.add(new Integer(int8));
                    break;

                case RPMIndexEntry.RPMTYPE_INT16:
                    numToRead = 2;
                    numRead = this.rpmin.read(readBuf, 0, numToRead);
                    if (numRead < numToRead)
                        break;

                    offset += numRead;
                    int int16 = (((readBuf[0] << 8) & 0x0000FF00) | (readBuf[1] & 0x000000FF));
                    if (this.debug)
                        System.err.println("INT16[" + cnt + "](" + numRead + ") = '" + int16 + "'");
                    values.add(new Integer(int16));
                    break;

                case RPMIndexEntry.RPMTYPE_INT32:
                    numToRead = 4;
                    numRead = this.rpmin.read(readBuf, 0, numToRead);
                    if (numRead < numToRead)
                        break;

                    offset += numRead;
                    int int32 = (((readBuf[0] << 24) & 0xFF000000) | ((readBuf[1] << 16) & 0x00FF0000) | ((readBuf[2] << 8) & 0x0000FF00) | (readBuf[3] & 0x000000FF));
                    if (this.debug)
                        System.err.println("INT32[" + cnt + "](" + numRead + ")<" + offset + "> = '" + int32 + "'");
                    values.add(new Integer(int32));
                    break;

                case RPMIndexEntry.RPMTYPE_INT64:
                    numToRead = 8;
                    numRead = this.rpmin.read(readBuf, 0, numToRead);
                    // UNDONE
                    if (numRead < numToRead)
                        break;

                    offset += numRead;
                    values.add(new Integer(-1));
                    break;

                case RPMIndexEntry.RPMTYPE_STRING:
                    StringBuffer sBuf = new StringBuffer();

                    for (;;) {
                        numToRead = 1;
                        numRead = this.rpmin.read(readBuf, 0, numToRead);
                        if (numRead < numToRead)
                            break;

                        offset += numRead;
                        if (readBuf[0] == 0)
                            break;

                        sBuf.append((char) readBuf[0]);
                    }

                    if (this.debug)
                        System.err.println("STRING (" + sBuf.length() + ") = '" + sBuf + "'");
                    values.add(sBuf.toString());
                    break;

                case RPMIndexEntry.RPMTYPE_BIN:
                    byte[] binData = new byte[entry.count];

                    numToRead = entry.count;
                    numRead = this.rpmin.read(binData, 0, numToRead);
                    if (this.debug)
                        System.err.println("BIN[" + cnt + "](" + numRead + ") read " + numRead + " bytes.");
                    if (numRead < numToRead)
                        break;

                    offset += numRead;
                    if (this.debug)
                        System.err.println("BIN[" + cnt + "](" + numRead + ")<" + offset + "> = '-B I N   D A T A-'");
                    values.add(binData);
                    cnt = entry.count; // end outer loop...
                    break;

                case RPMIndexEntry.RPMTYPE_STRING_ARRAY:
                    String[] sAry = new String[entry.count];
                    if (this.debug)
                        System.err.println("STRINGARRAY[" + cnt + "] has " + entry.count + " strings.");

                    for (int si = 0; si < entry.count; ++si) {
                        StringBuffer saBuf = new StringBuffer();

                        for (;;) {
                            numToRead = 1;
                            numRead = this.rpmin.read(readBuf, 0, numToRead);
                            if (numRead < numToRead)
                                break;

                            offset += numRead;
                            if (readBuf[0] == 0)
                                break;

                            saBuf.append((char) readBuf[0]);
                        }

                        if (this.debug)
                            System.err.println("STRINGARRAY[" + si + "] = '" + saBuf + "'");

                        sAry[si] = saBuf.toString();
                    }

                    values.add(sAry);
                    cnt = entry.count; // end outer loop...
                    break;

                case RPMIndexEntry.RPMTYPE_I18NSTRING:
                    break;
                }

                if (numToRead > 0 && numRead < numToRead)
                    throw new IOException("unexpected eof reading header entry #" + i);
            }

            entry.setValues(values);
        }

        if (offset < contentSize) {
            if (this.debug)
                System.err.println("SKIP CONTENT PAD: size = " + contentSize + " offset = " + offset);
            numToRead = (contentSize - offset);
            byte[] padBuf = new byte[numToRead];
            numRead = this.rpmin.read(padBuf, 0, numToRead);
            if (this.debug)
                System.err.println("SKIP CONTENT PAD: " + numRead + " of " + numToRead + " bytes skipped.");
            if (numRead < numToRead)
                throw new IOException("unexpected eof reading content pad");
        }
    }

    public CPIOEntry getNextFileEntry() throws IOException {
        return this.cpio.getNextEntry();
    }

    public int read(byte[] buf, int offset, int len) throws IOException {
        return this.cpio.read(buf, offset, len);
    }

    public void copyEntryContents(OutputStream out) throws IOException {
        byte[] buf = new byte[32 * 1024];

        for (;;) {
            int numRead = this.read(buf, 0, buf.length);
            if (numRead == -1)
                break;
            out.write(buf, 0, numRead);
        }
    }

    private int readShortInt() throws InvalidRPMFileException {
        try {
            int b1 = this.rpmin.read();
            int b2 = this.rpmin.read();

            if (b1 == -1 || b2 == -1)
                throw new InvalidRPMFileException("unexpected eof");

            return (b1 << 8) + b2;
        } catch (IOException ex) {
            throw new InvalidRPMFileException("IOException - " + ex.getMessage());
        }
    }

    private int readLongInt() throws InvalidRPMFileException {
        try {
            int b1 = this.rpmin.read();
            int b2 = this.rpmin.read();
            int b3 = this.rpmin.read();
            int b4 = this.rpmin.read();

            if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1)
                throw new InvalidRPMFileException("unexpected eof");

            return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
        } catch (IOException ex) {
            throw new InvalidRPMFileException("IOException - " + ex.getMessage());
        }
    }

}
