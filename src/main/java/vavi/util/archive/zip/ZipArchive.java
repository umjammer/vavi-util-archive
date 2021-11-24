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
    public ZipArchive(InputStream in) throws IOException {
        this.archive = new ZipInputStream(in);
    }

    @Override
    public void close() throws IOException {
        if (ZipFile.class.isInstance(archive)) {
            ZipFile.class.cast(archive).close();
        } else if (ZipInputStream.class.isInstance(archive)) {
            ZipInputStream.class.cast(archive).close();
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    public Entry<?>[] entries() {
    @Override
        if (ZipFile.class.isInstance(archive)) {
            List<Entry<?>> entries = new ArrayList<>();
            Enumeration<? extends java.util.zip.ZipEntry> e = ZipFile.class.cast(archive).entries();
            while (e.hasMoreElements()) {
                entries.add(new ZipEntry(e.nextElement()));
            }
            return entries.toArray(new Entry[entries.size()]);
        } else if (ZipInputStream.class.isInstance(archive)) {
            try {
                ZipInputStream zis = ZipInputStream.class.cast(archive);
                List<Entry<?>> entries = new ArrayList<>();
                java.util.zip.ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    entries.add(new ZipEntry(entry));
                }
                return entries.toArray(new Entry[entries.size()]);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    public Entry<?> getEntry(String name) {
        if (ZipFile.class.isInstance(archive)) {
            return new ZipEntry(ZipFile.class.cast(archive).getEntry(name));
        } else if (ZipInputStream.class.isInstance(archive)) {
            try {
                ZipInputStream zis = ZipInputStream.class.cast(archive);
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

    public InputStream getInputStream(Entry<?> entry) throws IOException {
    @Override
        if (ZipFile.class.isInstance(archive)) {
            return ZipFile.class.cast(archive).getInputStream(java.util.zip.ZipEntry.class.cast(entry.getWrappedObject()));
        } else if (ZipInputStream.class.isInstance(archive)) {
            ZipInputStream zis = ZipInputStream.class.cast(archive);
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
        if (ZipFile.class.isInstance(archive)) {
            return ZipFile.class.cast(archive).getName();
        } else if (ZipInputStream.class.isInstance(archive)) {
            return ZipInputStream.class.cast(archive).toString();
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    @Override
    public int size() {
        if (ZipFile.class.isInstance(archive)) {
            return ZipFile.class.cast(archive).size();
        } else if (ZipInputStream.class.isInstance(archive)) {
            return -1;
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }
}

/* */
