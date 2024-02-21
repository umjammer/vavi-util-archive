/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * NativeRarArchive represents RAR archive wrappring UnRAR32.dll.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 071110 nsano initial version <br>
 */
public class NativeRarArchive implements Archive {

    /** */
    private final List<CommonEntry> entries = new ArrayList<>();

    /** */
    private File file;

    /** Creates a RAR Archive instance. */
    public NativeRarArchive(File file) throws IOException {

        this.file = file;

System.err.println("file: " + file.getPath());
        openArchive(file.getPath(), 0);

        if (findFirst("*")) {
            do {
                CommonEntry entry = new CommonEntry();

                entry.setSize(getCurrentOriginalSize());
                entry.setCompressedSize(getCurrentCompressedSize());
                entry.setCrc(getCurrentCRC());
                entry.setMethod(getCurrentMethod());
                entry.setName(currentFilename);
                entry.setTime(DateUtil.dosDateTimeToLong(getCurrentDate(), getCurrentTime()));
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

    /** TODO */
    private static final String commandLineBase = "e \"{0}\" \"{1}\" \"{2}\"";

    /**
     * TODO password, keyfile
     */
    @Override
    public InputStream getInputStream(Entry entry) throws IOException {

        String tmp = System.getProperty("java.io.tmpdir");

        String commandLine = MessageFormat.format(commandLineBase,
                                                  file.getPath(),
                                                  tmp,
                                                  entry.getName());
Debug.println("commandLine: " + commandLine);

        exec(commandLine);

        Path temporaryFile = Path.of(tmp, entry.getName());
        if (!temporaryFile.normalize().startsWith(tmp)) {
            throw new IOException("Bad zip entry: " + entry.getName());
        }
        if (Files.exists(temporaryFile)) {
            return new BufferedInputStream(Files.newInputStream(temporaryFile));
        } else {
            throw new IOException("cannot extract: " + temporaryFile);
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
        System.loadLibrary("RarWrapper");
    }
}

/* */
