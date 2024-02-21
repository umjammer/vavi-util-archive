/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.apache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;
import vavi.util.archive.WrappedEntry;


/**
 * The wrapper for apache commons-compress.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 * @see "https://commons.apache.org/proper/commons-compress/examples.html"
 */
public class ApacheCommonsArchive extends InputStreamSupport implements Archive {

    /** */
    private Entry[] entries;

    /** */
    private File file;

    /** */
    public ApacheCommonsArchive(File file) {
        this.file = file;
    }

    /** */
    public ApacheCommonsArchive(InputStream is) throws IOException {
        super(is);
        this.file = this.archiveFileForInputStream;
    }

    @Override
    public void close() {
    }

    @Override
    public Entry[] entries() {
        if (this.entries == null) {

            List<ApacheEntry> entries = new ArrayList<>();
            try (InputStream is = new BufferedInputStream(Files.newInputStream(this.file.toPath()));
                 ArchiveInputStream<?> i = new ArchiveStreamFactory().createArchiveInputStream(is)) {
                ArchiveEntry entry;
                while ((entry = i.getNextEntry()) != null) {
                    if (!i.canReadEntryData(entry)) {
                        Debug.println(Level.INFO, "can not read, skip entry: " + (file != null ? file.toPath() + "/" : "") + entry.getName());
                        continue;
                    }
                    entries.add(new ApacheEntry(entry));
                }
            } catch (ArchiveException e) {
                throw new IllegalStateException(e);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            this.entries = entries.toArray(new ApacheEntry[0]);
        }
        return this.entries;
    }

    @Override
    public Entry getEntry(String name) {
        return Arrays.stream(entries()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    /** WARNING: available does not work */
    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        try {
            InputStream is = new BufferedInputStream(Files.newInputStream(this.file.toPath()));
            ArchiveInputStream<?> i = new ArchiveStreamFactory().createArchiveInputStream(is);
            ArchiveEntry e;
            while ((e = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(e)) {
Debug.println(Level.INFO, "can not read, skip entry: " + file.toPath() + "/" + entry.getName());
                    continue;
                }
                if (((WrappedEntry<?>) entry).getWrappedObject().equals(e)) {
                    return i;
                }
            }
        } catch (ArchiveException e) {
            throw new IOException(e);
        }
        throw new IllegalStateException(entry.getName());
    }

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public int size() {
        return entries().length;
    }
}

/* */
