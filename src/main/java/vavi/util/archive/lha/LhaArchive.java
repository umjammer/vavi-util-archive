/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;
import vavi.util.archive.WrappedEntry;


/**
 * Represents LHA archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class LhaArchive extends InputStreamSupport implements Archive {

    /** */
    private LhaFile archive;

    private String name;

    /** */
    private Entry[] entries;

    /** */
    public LhaArchive(File file) throws IOException {
        this.archive = new LhaFile(file);
        this.name = file.getName();
    }

    /** */
    public LhaArchive(InputStream is) throws IOException {
        super(is);
        this.archive = new LhaFile(this.archiveFileForInputStream);
        this.name = archiveFileForInputStream.getName();
    }

    @Override
    public void close() throws IOException {
        archive.close();
    }

    @Override
    public Entry[] entries() {
        if (entries == null) {
            LhaHeader[] headers = archive.getEntries();
            entries = new Entry[headers.length];
            for (int i = 0; i < headers.length; i++) {
                entries[i] = new LhaEntry(headers[i]);
            }
        }
        return entries;
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

    @Override
    public InputStream getInputStream(Entry entry) throws IOException {
        return archive.getInputStream((LhaHeader) ((WrappedEntry<?>) entry).getWrappedObject());
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
