/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;
import vavi.util.archive.gca.NativeGcaArchive;
import vavi.util.win32.DateUtil;

import static java.lang.System.getLogger;


/**
 * The wrapper class for 7-zip32.dll.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030228 nsano initial version <br>
 */
public class NativeSevenZipArchive implements Archive {

    private static final Logger logger = getLogger(NativeSevenZipArchive.class.getName());

    /** */
    private final List<CommonEntry> entries = new ArrayList<>();

    /** */
    private final File file;

    /** */
    public NativeSevenZipArchive(File file) throws IOException {
logger.log(Level.DEBUG, "7-zip32.dll: " + getVersion());

        this.file = file;

        openArchive(file.getPath(), 0);

        if (findFirst("*")) {
            do {
                CommonEntry entry = new CommonEntry();

                entry.setSize(getCurrentOriginalSize());
                entry.setCompressedSize(getCurrentCompressedSize());
                entry.setCrc(getCurrentCRC());
                entry.setMethod(getCurrentMethod());
                entry.setName(currentFilename);
                entry.setTime(DateUtil.dosDateTimeToLong(getCurrentDate(),
                                                         getCurrentTime()));
                entries.add(entry);
logger.log(Level.DEBUG, StringUtil.paramString(entry));
logger.log(Level.DEBUG, "time: " + new Date(entry.getTime()));
            } while (findNext());
        }
    }

    @Override
    public void close() throws IOException {
        closeArchive();
    }

    @Override
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {

        File temporaryDirectory = new File(System.getProperty("java.io.tmpdir"));
        String temporaryDirectoryString = temporaryDirectory.getAbsolutePath();
logger.log(Level.DEBUG, "temporaryDirectory: " + temporaryDirectoryString);

        String commandLine = MessageFormat.format("x -hide -y \"{0}\" -o\"{1}\" \"{2}\"",
                                                  file.getPath(),
                                                  temporaryDirectoryString,
                                                  entry.getName());
logger.log(Level.DEBUG, "commandLine: " + commandLine);

try {
        exec(commandLine);
} catch (IOException e) {
 try {
  int code = Integer.parseInt(e.getMessage());
  logger.log(Level.TRACE, String.format("%1$d, 0x%1$04x\n", code));
  logger.log(Level.TRACE, rb.getString(errorCodeTable.getProperty(String.format("0x%04X", code))));
 } catch (Exception e2) {
  logger.log(Level.ERROR, e2.getMessage(), e2);
 }
 throw e;
}

        Path temporaryFile = Path.of(temporaryDirectoryString, entry.getName());
        if (!temporaryFile.normalize().startsWith(temporaryDirectoryString)) {
            throw new IOException("Bad zip entry: " + entry.getName());
        }
        if (Files.exists(temporaryFile)) {
            return new BufferedInputStream(Files.newInputStream(temporaryFile));
        } else {
            throw new IOException("cannpt extract: " + temporaryFile);
        }
    }

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public int size() {
        return entries.size();
    }

    /** <codeText,localString> */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.util.archive.vavi.NativeSevenZipArchive", Locale.getDefault());

    /** <hexCode,codeText> */
    private static Properties errorCodeTable;

    /* */
    static {
        String path = "NativeSevenZipArchive_errorCode.properties";
        try {
            errorCodeTable = new Properties();
            errorCodeTable.load(NativeGcaArchive.class.getResourceAsStream(path));
        } catch (Exception e) {
logger.log(Level.ERROR, e.getMessage(), e);
        }
    }

    // for Native method ----

    /** instance id for dll */
    private int instance;

    /** getFileName() doesn't work well, so this is alternative */
    private String currentFilename;

    // interface

    /** processes each functionality by a command string */
    private native void exec(String command) throws IOException;
    /** gets version */
    private native int getVersion();
    /** tells is running or not */
    private native boolean isRunning();

    /** checks the archive is valid or not */
    private native boolean checkArchive(String filename, int mode);
    /** gets entries in an archive */
    private native int getFileCount(String filename) throws IOException;
    /** open an archive */
    private native void openArchive(String filename, int mode) throws IOException;
    /** close the archive */
    private native void closeArchive() throws IOException;
    /** gets the first entry */
    private native boolean findFirst(String key) throws IOException;
    /** gets after 2nd entry */
    private native boolean findNext();

    /** gets the current entry filename */
    private native String getCurrentFileName() throws IOException;
    /** gets the current entry archive method */
    private native int getCurrentMethod() throws IOException;
    /** gets the current entry original size */
    private native long getCurrentOriginalSize() throws IOException;
    /** gets the current entry compressed size */
    private native long getCurrentCompressedSize() throws IOException;
    /** gets the current entry date in DOS format */
    private native int getCurrentDate() throws IOException;
    /** gets the current entry time in DOS format */
    private native int getCurrentTime() throws IOException;
    /** gets the current entry checksum */
    private native long getCurrentCRC() throws IOException;

    /** gets a sum of selected entries size */
    private native long getSelectedSize() throws IOException;
    /** gets a sum of selected entries compressed size */
    private native long getSelectedCompressedSize() throws IOException;
    /** gets compression ratio of selected entries */
    private native int getSelectedRatio() throws IOException;

    /* */
    static {
        System.loadLibrary("SevenZipWrapper");
    }
}
