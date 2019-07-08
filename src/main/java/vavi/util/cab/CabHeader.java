/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.cab;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import vavi.io.LittleEndianDataInputStream;
import vavi.io.UtilInputStream;


/**
 * Encapsulates a CFHEADER entry.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040929 nsano initial version <br>
 */
class CabHeader {
    /** reserved */
    private byte[] reserved1 = new byte[4];
    /** size of this cabinet file in bytes */
    private int fileSize = 0;
    /** reserved */
    private byte[] reserved2 = new byte[4];
    /** offset of the first CabFile entry */
    private int offsetOfFirstCFFILEEntry;
    /** reserved */
    private byte[] reserved3 = new byte[4];
    /** cabinet file format version, minor */
    private byte versionMinor = 3;
    /** cabinet file format version, major */
    private byte versionMajor = 1;
    /** true iff there is a cabinet before this one in a sequence */
    private boolean prevCAB = false;
    /** true iff there is a cabinet after this one in a sequence */
    private boolean nextCAB = false;
    /** true iff the cab has per-{cabinet, folder, block} reserved areas */
    private boolean hasReserved = false;
    /** must be the same for all cabinets in a set */
    private int setID = 0;
    /** number of this cabinet file in a set */
    private int indexInCabinetSet = 0;
    /** (optional) size of per-folder reserved area */
    private byte perCFFOLDERReservedSize = 0;
    /** (optional) size of per-datablock reserved area */
    private byte perDatablockReservedSize = 0;
    /** per-cabinet reserved area */
    private byte[] perCabinetReservedArea = null;
    /** name of previous cabinet file in a set */
    @SuppressWarnings("unused")
    private String previousCabinet = null;
    /** name of previous disk in a set */
    @SuppressWarnings("unused")
    private String previousDisk = null;
    /** name of next cabinet in a set */
    @SuppressWarnings("unused")
    private String nextCabinet = null;
    /** name of next disk in a set */
    @SuppressWarnings("unused")
    private String nextDisk = null;
    /** */
    private int folderNumber;
    /** */
    private int fileNumber;

    /**
     * @return Returns the files.
     */
    public int getFileNumber() {
        return fileNumber;
    }

    /**
     * @return Returns the folders.
     */
    public int getFolderNumber() {
        return folderNumber;
    }

    /**
     * @return Returns the perCFFOLDERReservedSize.
     */
    public int getPerCFFOLDERReservedSize() {
        return perCFFOLDERReservedSize;
    }

    /** */
    void print(PrintStream ps) {
        ps.println("CAB CabFile CabHeader v" + ((int) versionMajor) + "." +
                   ((int) versionMinor));
        ps.println("    total file size               = " + fileSize);
        ps.println("    offset of first file          = " +
                   offsetOfFirstCFFILEEntry);
        ps.println("    total folders                 = " + folderNumber);
        ps.println("    total files                   = " + fileNumber);
        ps.println("    flags                         = 0x" +
                   Integer.toString((prevCAB ? 0x1 : 0x0) |
                                    (nextCAB ? 0x2 : 0x0) |
                                    (hasReserved ? 0x4 : 0x0), 16) + " [ " +
                   (prevCAB ? "prev " : "") + (nextCAB ? "next " : "") +
                   (hasReserved ? "reserve_present " : "") + "]");
        ps.println("    set id                        = " + setID);
        ps.println("    index in set                  = " +
                   indexInCabinetSet);
        ps.println("    header reserved area #1       =" + " 0x" +
                   Integer.toString(reserved1[0], 16) + " 0x" +
                   Integer.toString(reserved1[1], 16) + " 0x" +
                   Integer.toString(reserved1[2], 16) + " 0x" +
                   Integer.toString(reserved1[3], 16));
        ps.println("    header reserved area #2       =" + " 0x" +
                   Integer.toString(reserved2[0], 16) + " 0x" +
                   Integer.toString(reserved2[1], 16) + " 0x" +
                   Integer.toString(reserved2[2], 16) + " 0x" +
                   Integer.toString(reserved2[3], 16));
        ps.println("    header reserved area #3       =" + " 0x" +
                   Integer.toString(reserved3[0], 16) + " 0x" +
                   Integer.toString(reserved3[1], 16) + " 0x" +
                   Integer.toString(reserved3[2], 16) + " 0x" +
                   Integer.toString(reserved3[3], 16));
        if (hasReserved) {
            if (perCabinetReservedArea != null) {
                ps.print("    per-cabinet reserved area     = ");
                for (int i = 0; i < perCabinetReservedArea.length; i++) {
                    ps.print((((perCabinetReservedArea[i] & 0xff) < 16)
                              ? "0" : "") +
                             Integer.toString(perCabinetReservedArea[i] &
                                              0xff, 16) + " ");
                }
                ps.println();
            }
            ps.println("    per folder  reserved area     = " +
                       perCFFOLDERReservedSize + " bytes");
            ps.println("    per block   reserved area     = " +
                       perDatablockReservedSize + " bytes");
        }
    }

    /** */
    public String toString() {
        return "[ CAB CabFile CabHeader v" + ((int) versionMajor) + "." +
               ((int) versionMinor) + ", " + fileSize + " bytes, " +
               folderNumber + " folders, " + fileNumber + " files] ";
    }

    /** fills in all fields in the header and positions the stream at the first folder */
    public void read(InputStream is) throws IOException {

        @SuppressWarnings("resource")
        LittleEndianDataInputStream ledis = new LittleEndianDataInputStream(is);
        @SuppressWarnings("resource")
        UtilInputStream uis = new UtilInputStream(is);

        ledis.readFully(reserved1);

        byte[] headerHashable = new byte[28];
        ledis.readFully(headerHashable);

        fileSize = ledis.readInt();
        ledis.readFully(reserved2);
        offsetOfFirstCFFILEEntry = ledis.readInt();
        ledis.readFully(reserved3);
        versionMinor = ledis.readByte();
        versionMajor = ledis.readByte();
        folderNumber = ledis.readShort();
        fileNumber = ledis.readShort();

        int flags = ledis.readShort();
        prevCAB = (flags & 0x0001) != 0;
        nextCAB = (flags & 0x0002) != 0;
        hasReserved = (flags & 0x0004) != 0;
        setID = ledis.readShort();
        indexInCabinetSet = ledis.readShort();

        if ((offsetOfFirstCFFILEEntry < 0) || (fileSize < 0)) {
            throw new IOException("bogus header");
        }

        if (hasReserved) {
            perCabinetReservedArea = new byte[ledis.readShort()];
            perCFFOLDERReservedSize = ledis.readByte();
            perDatablockReservedSize = ledis.readByte();
            if (perCabinetReservedArea.length > 0) {
                ledis.readFully(perCabinetReservedArea);
            }
        }

        if (prevCAB) {
            previousCabinet = uis.readAsciiz();
            previousDisk = uis.readAsciiz();
        }
        if (nextCAB) {
            nextCabinet = uis.readAsciiz();
            nextDisk = uis.readAsciiz();
        }
    }
}

/* */
