/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.stuffit;

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
import java.util.List;

import vavi.util.archive.Archive;
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;

import static java.lang.System.getLogger;


/**
 * NativeStuffItArchive represents a StuffIt archive wrapping StuffItWrapper.dll.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030226 nsano initial version <br>
 *          0.01 030228 nsano implements archive <br>
 */
public class NativeStuffItArchive implements Archive {

    private static final Logger logger = getLogger(NativeStuffItArchive.class.getName());

    /** */
    private final List<CommonEntry> entries = new ArrayList<>();

    /** */
    private final File file;

    /** */
    public NativeStuffItArchive(File file) throws IOException {
logger.log(Level.TRACE, "StuffIt.dll: " + getVersion());

        this.file = file;

        openArchive(file.getPath(), 0);
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

        String tmp = System.getProperty("java.io.tmpdir");

        String commandLine = MessageFormat.format("e \"{0}\" \"{1}\" \"{2}\"",
                                                  file.getPath(),
                                                  tmp,
                                                  entry.getName());
logger.log(Level.DEBUG, "commandLine: " + commandLine);

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

    /**
     * from JNI
     * @param filename TODO unicode
     * @param time TODO unused
     * @param size before compress
     * @param compressedSize after compress
     * @param crc TODO unused
     * @param method TODO unused
     */
    protected void addFileEntry(String filename, long time, int size, int compressedSize, int crc, int method) {
        CommonEntry entry = new CommonEntry();

        entry.setSize(size);
        entry.setCompressedSize(compressedSize);
        entry.setCrc(crc);
        entry.setMethod(method);
        entry.setName(filename);
        entry.setTime(time);

        entries.add(entry);
    }

    /**
     * from JNI
     * @param filename TODO unicode
     * @param time TODO unused
     */
    protected void addDirectoryEntry(String filename, long time) {
        CommonEntry entry = new CommonEntry();

        entry.setDirectory(true);
        entry.setName(filename);
        entry.setTime(time);

        entries.add(entry);
    }

    // for Native method ----

    /** instance id for dll */
    private int instance;

    // interface

    /** processes each functionality by a command string */
    private native void exec(String command) throws IOException;
    /** gets version */
    protected native int getVersion();

    /** open an archive */
    private native void openArchive(String filename, int mode) throws IOException;
    /** close the archive */
    private native void closeArchive() throws IOException;

    /* */
    static {
        System.loadLibrary("StuffItWrapper");
    }
}
