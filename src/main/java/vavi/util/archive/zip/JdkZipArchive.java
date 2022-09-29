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
import java.util.zip.ZipFile;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;


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
public class JdkZipArchive extends InputStreamSupport implements Archive {

    /** encoding for zip entry names */
    public static final String ZIP_ENCODING = "vavi.util.archive.zip.encoding";

    /** */
    private ZipFile archive;

    /** */
    private Entry[] entries;

    /** */
    public JdkZipArchive(File file) throws IOException {
        String encoding = System.getProperty(ZIP_ENCODING, StandardCharsets.UTF_8.toString());
        this.archive = new ZipFile(file, Charset.forName(encoding));
        this.entries = entries();
    }

    /**
     * @param failsafeEncoding when open w/ {@link #ZIP_ENCODING}, try again to open using this encoding
     */
    public JdkZipArchive(File file, String failsafeEncoding) throws IOException {
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
    public JdkZipArchive(InputStream in) throws IOException {
        super(in);
        this.archive = new ZipFile(this.archiveFileForInputStream);
    }

    @Override
    public void close() throws IOException {
        if (archive != null) {
            archive.close();
        }
    }

    @Override
    public final Entry[] entries() {
        if (this.entries == null) {
            List<Entry> entries = new ArrayList<>();
            Enumeration<? extends java.util.zip.ZipEntry> e = archive.entries();
            while (e.hasMoreElements()) {
                entries.add(new JdkZipEntry(e.nextElement()));
            }
            this.entries = entries.toArray(new Entry[0]);
        }
        return this.entries;
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries()) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    /** WARNING: available does not work */
    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        return archive.getInputStream(((JdkZipEntry) entry).getWrappedObject());
    }

    @Override
    public String getName() {
        return archive.getName();
    }

    @Override
    public int size() {
        return entries().length;
    }
}

/* */
