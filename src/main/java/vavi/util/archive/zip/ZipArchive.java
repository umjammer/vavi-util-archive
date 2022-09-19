/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * This class is used to read entries from a zip file.
 * <p>
 * system property
 * <li> "vavi.util.archive.zip.encoding" ({@link #ZIP_ENCODING}) ... encoding for zip entry names
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class ZipArchive implements Archive {

    /** encoding for zip entry names */
    public static final String ZIP_ENCODING = "vavi.util.archive.zip.encoding";

    /** */
    private Object archive;

    /** */
    private Entry[] entries;

    /** */
    public ZipArchive(File file) throws IOException {
        String encoding = System.getProperty(ZIP_ENCODING, StandardCharsets.UTF_8.toString());
        this.archive = new ZipFile(file, Charset.forName(encoding));
        this.entries = entries();
    }

    /**
     * @param failsafeEncoding when open w/ {@link #ZIP_ENCODING}, try again to open using this encoding
     */
    public ZipArchive(File file, String failsafeEncoding) throws IOException {
        try {
            String encoding = System.getProperty(ZIP_ENCODING, StandardCharsets.UTF_8.toString());
            this.archive = new ZipFile(file, Charset.forName(encoding));
            this.entries = entries();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("MALFORMED")) {
Debug.println("zip reading failure by utf-8, retry using " + failsafeEncoding);
                this.archive = new ZipFile(file, Charset.forName(failsafeEncoding));
                this.entries = entries();
            } else {
                throw e;
            }
        }
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
    public final Entry[] entries() {
        if (this.entries != null) {
            return entries;
        }

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

    @Override
    public Entry getEntry(String name) {
        if (archive instanceof ZipFile) {
            ZipEntry entry = new ZipEntry(((ZipFile) archive).getEntry(name));
if (entry.getWrappedObject() == null) {
 Debug.println(Level.FINE, "entry not found for name: " + name);
}
            return entry;
        } else if (archive instanceof ZipInputStream) {
            try {
                ZipInputStream zis = (ZipInputStream) archive;
                java.util.zip.ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (name.equals(entry.getName())) {
                        return new ZipEntry(entry);
                    }
                }
Debug.println(Level.FINE, "entry not found for name: " + name);
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
