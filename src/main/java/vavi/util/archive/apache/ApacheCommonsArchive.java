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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.WrappedEntry;


/**
 * The wrapper for Apache commons compress.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 * @see "https://commons.apache.org/proper/commons-compress/examples.html"
 */
public class ApacheCommonsArchive implements Archive {

    /** */
    private List<Entry> entries = new ArrayList<>();

    /** */
    private File file;

    /** */
    public ApacheCommonsArchive(File file) throws IOException {
        this(new BufferedInputStream(Files.newInputStream(file.toPath())));
        this.file = file;
    }

    /** */
    public ApacheCommonsArchive(InputStream is) throws IOException {
        try (ArchiveInputStream i = new ArchiveStreamFactory().createArchiveInputStream(is)) {
            ArchiveEntry entry;
            while ((entry = i.getNextEntry()) != null) {
                if (!i.canReadEntryData(entry)) {
Debug.println(Level.INFO, "can not read, skip entry: " + (file != null ? file.toPath() + "/" : "") + entry.getName());
                    continue;
                }
                entries.add(new ApacheEntry(entry));
            }
        } catch (ArchiveException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        return entries.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        try (ArchiveInputStream i = new ArchiveStreamFactory().createArchiveInputStream(
                new BufferedInputStream(Files.newInputStream(file.toPath())))) {
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
        throw new NoSuchElementException(entry.getName()); // TODO
    }

    @Override
    public String getName() {
        return file != null ? file.getPath() : null;
    }

    @Override
    public int size() {
        return entries.size();
    }
}

/* */