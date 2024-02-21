/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.tar;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.WrappedEntry;


/**
 * TarArchive represents TAR archive file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 */
public class TarArchive implements Archive {

    /** */
    private TarArchiveInputStream archive;

    /** */
    private final Entry[] entries;

    /** */
    private String name;

    /** TODO input stream will be consumed */
    public TarArchive(InputStream is) throws IOException {
        this.archive = new TarArchiveInputStream(is);

        List<Entry> list = new ArrayList<>();
        while (true) {
            org.apache.commons.compress.archivers.tar.TarArchiveEntry e = archive.getNextEntry();
            if (e == null) {
                break;
            }
            list.add(new TarEntry(e));
        }

        this.entries = new Entry[list.size()];

        list.toArray(entries);
    }

    @Override
    public void close() throws IOException {
        archive.close();
    }

    @Override
    public Entry[] entries() {
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        throw new NoSuchElementException(name);
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        for (Entry value : entries) {
            if (value.equals(entry)) {
                org.apache.tools.tar.TarEntry e =
                        (org.apache.tools.tar.TarEntry)
                                ((WrappedEntry<?>) value).getWrappedObject();
                return Files.newInputStream(e.getFile().toPath());
            }
        }
        throw new NoSuchElementException(entry.getName());
    }

    @Override
    public String getName() {
        return name;
    }

    /** */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int size() {
        return entries.length;
    }
}

/* */
