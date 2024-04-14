/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;
import vavi.util.archive.WrappedEntry;


/**
 * The wrapper for Apache commons compress.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 */
public class ApacheSevenZipArchive extends InputStreamSupport implements Archive {

    /** */
    private SevenZFile file;

    /** */
    private String name;

    /** */
    private Entry[] entries;

    /** */
    public ApacheSevenZipArchive(File file) throws IOException {
        this.file = new SevenZFile(file);
        this.name = file.getPath();
    }

    /** */
    public ApacheSevenZipArchive(InputStream is) throws IOException {
        super(is);
        this.file = new SevenZFile(archiveFileForInputStream);
        this.name = archiveFileForInputStream.getPath();
    }

    @Override
    public void close() throws IOException {
        file.close();
    }

    @Override
    public Entry[] entries() {
        if (entries == null) {
            entries = StreamSupport.stream(file.getEntries().spliterator(), false)
                    .map(ApacheSevenZipEntry::new)
                    .toArray(ApacheSevenZipEntry[]::new);
        }
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        return Arrays.stream(entries)
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        return file.getInputStream((SevenZArchiveEntry) ((WrappedEntry<?>) entry).getWrappedObject());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        return entries().length;
    }
}
