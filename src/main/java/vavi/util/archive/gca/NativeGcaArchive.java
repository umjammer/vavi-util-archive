/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.gca;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;
import vavi.util.win32.DateUtil;


/**
 * NativeGcaArchive repesents GCA archive wrapping UnGCA32.dll.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030226 nsano initial version <br>
 *          0.01 030228 nsano implements archive <br>
 */
public class NativeGcaArchive implements Archive {

    /** */
    private List<CommonEntry> entries = new ArrayList<>();

    /** */
    private File file;

    /** */
    public NativeGcaArchive(File file) throws IOException {

        this.file = file;

        openArchive(file.getPath(), 0);

        if (findFirst("*.*")) {
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
System.err.println(StringUtil.paramString(entry));
System.err.println("time: " + new Date(entry.getTime()));
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

    /** */
    private static final String commandLineBase = "e \"{0}\" \"{1}\" \"{2}\"";

    /**
     * TODO password, keyfile
     */
    @Override
    public InputStream getInputStream(Entry entry) throws IOException {

        String commandLine = MessageFormat.format(commandLineBase,
                                                  file.getPath(),
                                                  System.getProperty("java.io.tmpdir"),
                                                  entry.getName());
Debug.println("commandLine: " + commandLine);

        exec(commandLine);

        String temporaryFileName = System.getProperty("java.io.tmpdir") + entry.getName();
        File temporaryFile = new File(temporaryFileName);
        if (temporaryFile.exists()) {
            return new BufferedInputStream(Files.newInputStream(temporaryFile.toPath()));
        } else {
            throw new IOException("cannot extract: " + temporaryFileName);
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

    // for Native method ----

    /** instance id for dll */
    private int instance;

    /** getFileName() doesn't work well, so this is alternative */
    private String currentFilename;

    // interface

    /** processes each functionality by a command string */
    private native void exec(String command) throws IOException;
    /** gets version */
    protected native int getVersion();
    /** tells is running or not */
    private native boolean isRunning();

    /** checks the archive is valid or not */
    private native boolean checkArchive(String filename, int mode);
    /** gets entries count in an archive */
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
        System.loadLibrary("GcaWrapper");
    }
}

/* */
