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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.tools.zip.ZipFile;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;


/**
 * The service provider for ZIP archive using the Ant zip library.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class AntZipArchive extends InputStreamSupport implements Archive {

    /** */
    private final ZipFile archive;

    /** */
    private final String name;

    /** */
    private Entry[] entries;

    /** */
    public AntZipArchive(File file) throws IOException {
        this.archive = new ZipFile(file);
        this.name = file.getPath();
    }

    /** */
    public AntZipArchive(InputStream is) throws IOException {
        super(is);
        this.archive = new ZipFile(archiveFileForInputStream);
        this.name = archiveFileForInputStream.getPath();
    }

    @Override
    public void close() throws IOException {
        archive.close();
    }

    @Override
    public Entry[] entries() {
        if (this.entries == null) {
            List<AntZipEntry> entries = new ArrayList<>();
            Enumeration<org.apache.tools.zip.ZipEntry> e = archive.getEntries();
            for (int i = 0; e.hasMoreElements(); i++) {
                entries.add(new AntZipEntry(e.nextElement()));
            }
            this.entries = entries.toArray(new AntZipEntry[0]);
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
        return archive.getInputStream(((AntZipEntry) entry).getWrappedObject());
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
