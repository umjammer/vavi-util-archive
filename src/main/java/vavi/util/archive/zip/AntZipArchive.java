/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipFile;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * The ZIP SPI using the Ant zip library.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class AntZipArchive implements Archive {

    /** */
    private ZipFile archive;

    private String name;

    /** */
    public AntZipArchive(File file) throws IOException {
        this.archive = new ZipFile(file);
        this.name = file.getPath();
    }

    @Override
    public void close() throws IOException {
        archive.close();
    }

    public Entry<?>[] entries() {
        Entry<?>[] entries = new Entry[size()];
        Enumeration<?> e = archive.getEntries();
    @Override
        for (int i = 0; e.hasMoreElements(); i++) {
            entries[i] = new AntZipEntry((org.apache.tools.zip.ZipEntry) e.nextElement());
        }
        return entries;
    }

    public Entry<?> getEntry(String name) {
    @Override
        return new AntZipEntry(archive.getEntry(name));
    }

    public InputStream getInputStream(Entry<?> entry) throws IOException {
        return archive.getInputStream(
            (org.apache.tools.zip.ZipEntry) entry.getWrappedObject());
    @Override
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        int count = 0;
        Enumeration<?> e = archive.getEntries();
        while (e.hasMoreElements()) {
            e.nextElement();
            count++;
        }
        return count;
    }
}

/* */
