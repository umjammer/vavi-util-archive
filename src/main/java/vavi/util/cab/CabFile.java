/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.cab;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import vavi.io.LittleEndianDataInputStream;
import vavi.io.UtilInputStream;
import vavi.util.win32.DateUtil;


/**
 * Encapsulates a CFFILE entry
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040929 nsano initial version <br>
 */
public class CabFile {
    /** size of this file */
    private int fileSize = 0;
    /** offset of this file within the folder, not accounting for compression */
    @SuppressWarnings("unused")
    private int uncompressedOffsetInCFFOLDER = 0;
    /** index of the CabFolder we belong to */
    private int folderIndex = 0;
    /** modification date */
    @SuppressWarnings("unused")
    private Date date = null;
    /** attrs */
    private int attrs = 0;
    /** read-only flag */
    private boolean readOnly = false;
    /** hidden flag */
    private boolean hidden = false;
    /** system flag */
    private boolean system = false;
    /** archive flag */
    private boolean arch = false;
    /** true if file should be run during extraction */
    private boolean runAfterExec = false;
    /** true if filename is UTF-encoded */
    private boolean UTFfileName = false;
    /** filename */
    private String fileName = null;

    /**
     * @return Returns the folderIndex.
     */
    public int getFolderIndex() {
        return folderIndex;
    }

    /**
     * @return Returns the fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return Returns the fileSize.
     */
    public int getFileSize() {
        return fileSize;
    }

    /** */
    public CabFile() {
    }

    /** */
    public String toString() {
        return "[ CAB CabFile: " + fileName + ", " + fileSize + " bytes [ " +
               (readOnly ? "readonly " : "") + (system ? "system " : "") +
               (hidden ? "hidden " : "") + (arch ? "arch " : "") +
               (runAfterExec ? "run_after_exec " : "") +
               (UTFfileName ? "UTF_filename " : "") + "]";
    }

    /** */
    public void read(InputStream is) throws IOException {
        @SuppressWarnings("resource")
        LittleEndianDataInputStream ledis = new LittleEndianDataInputStream(is);
        @SuppressWarnings("resource")
        UtilInputStream uis = new UtilInputStream(is);

        fileSize = ledis.readInt();
        uncompressedOffsetInCFFOLDER = ledis.readInt();
        folderIndex = ledis.readShort();
        int date = ledis.readShort();
        int time = ledis.readShort();
        this.date = new Date(DateUtil.dosDateTimeToLong(date, time));
        attrs = ledis.readShort();
        readOnly = (attrs & 0x1) != 0;
        hidden = (attrs & 0x2) != 0;
        system = (attrs & 0x4) != 0;
        arch = (attrs & 0x20) != 0;
        runAfterExec = (attrs & 0x40) != 0;
        UTFfileName = (attrs & 0x80) != 0;
        fileName = uis.readAsciiz();
    }
}

/* */
