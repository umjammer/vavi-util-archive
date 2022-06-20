/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * This class is used to read entries from a zip file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class ZipArchive implements Archive {

    /** */
    private Object archive;

    /** */
    public ZipArchive(File file) throws IOException {
        this.archive = new ZipFile(file);
    }

    /** */
    public ZipArchive(InputStream in) {
        this.archive = new ZipInputStream(in);
    }

    @Override
    public void close() throws IOException {
        if (archive instanceof ZipFile) {
            ((ZipFile) archive).close();
        } else if (archive instanceof ZipInputStream) {
            ((ZipInputStream) archive).close();
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    @Override
    public Entry[] entries() {
        if (archive instanceof ZipFile) {
            List<Entry> entries = new ArrayList<>();
            Enumeration<? extends java.util.zip.ZipEntry> e = ((ZipFile) archive).entries();
            while (e.hasMoreElements()) {
                entries.add(new ZipEntry(e.nextElement()));
            }
            return entries.toArray(new Entry[0]);
        } else if (archive instanceof ZipInputStream) {
            try {
                ZipInputStream zis = (ZipInputStream) archive;
                List<Entry> entries = new ArrayList<>();
                java.util.zip.ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    entries.add(new ZipEntry(entry));
                }
                return entries.toArray(new Entry[0]);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    public Entry getEntry(String name) {
        if (archive instanceof ZipFile) {
            return new ZipEntry(((ZipFile) archive).getEntry(name));
        } else if (archive instanceof ZipInputStream) {
            try {
                ZipInputStream zis = (ZipInputStream) archive;
                java.util.zip.ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (name.equals(entry.getName())) {
                        return new ZipEntry(entry);
                    }
                }
                return null;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        if (archive instanceof ZipFile) {
            return ((ZipFile) archive).getInputStream(((ZipEntry) entry).getWrappedObject());
        } else if (archive instanceof ZipInputStream) {
            ZipInputStream zis = (ZipInputStream) archive;
            java.util.zip.ZipEntry header;
            while ((header = zis.getNextEntry()) != null) {
                if (entry.getName().equals(header.getName())) {
                    return zis;
                }
            }
            return null;
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    @Override
    public String getName() {
        if (archive instanceof ZipFile) {
            return ((ZipFile) archive).getName();
        } else if (archive instanceof ZipInputStream) {
            return archive.toString();
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    @Override
    public int size() {
        if (archive instanceof ZipFile) {
            return ((ZipFile) archive).size();
        } else if (archive instanceof ZipInputStream) {
            return -1;
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }
}

/* */
