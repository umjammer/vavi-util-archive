/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.WrappedEntry;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import jp.gr.java_conf.dangan.util.lha.LhaInputStream;


/**
 * Represents LHA archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class LhaArchive implements Archive {

    /** */
    private Object archive;

    /** */
    public LhaArchive(File file) throws IOException {
        this.archive = new LhaFile(file);
    }

    /** */
    public LhaArchive(InputStream is) {
        this.archive = new LhaInputStream(is);
    }

    @Override
    public void close() throws IOException {
        if (archive instanceof LhaFile) {
            ((LhaFile) archive).close();
        } else if (archive instanceof LhaInputStream) {
            ((LhaInputStream) archive).close();
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    @Override
    public Entry[] entries() {
        if (archive instanceof LhaFile) {
            LhaHeader[] headers = ((LhaFile) archive).getEntries();
            Entry[] entries = new Entry[headers.length];
            for (int i = 0; i < headers.length; i++) {
                entries[i] = new LhaEntry(headers[i]);
            }
            return entries;
        } else if (archive instanceof LhaInputStream) {
            try {
                LhaInputStream lis = (LhaInputStream) archive;
                List<Entry> entries = new ArrayList<>();
                LhaHeader header;
                while ((header = lis.getNextEntry()) != null) {
                    entries.add(new LhaEntry(header));
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
        if (archive instanceof LhaFile) {
            LhaHeader[] headers = ((LhaFile) archive).getEntries();
            for (LhaHeader header : headers) {
                if (header.getPath().equals(name)) {
                    return new LhaEntry(header);
                }
            }
            return null;
        } else if (archive instanceof LhaInputStream) {
            try {
                LhaInputStream lis = (LhaInputStream) archive;
                LhaHeader header;
                while ((header = lis.getNextEntry()) != null) {
                    if (name.equals(header.getPath())) {
                        return new LhaEntry(header);
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
        if (archive instanceof LhaFile) {
            return ((LhaFile) archive).getInputStream((LhaHeader) ((WrappedEntry<?>) entry).getWrappedObject());
        } else if (archive instanceof LhaInputStream) {
            LhaInputStream lis = (LhaInputStream) archive;
            LhaHeader header;
            while ((header = lis.getNextEntry()) != null) {
                if (entry.getName().equals(header.getPath())) {
                    return lis;
                }
            }
            return null;
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    @Override
    public String getName() {
        if (archive instanceof LhaFile) {
            return null; // TODO LhaFile.class.cast(archive).;
        } else if (archive instanceof LhaInputStream) {
            return archive.toString();
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    @Override
    public int size() {
        if (archive instanceof LhaFile) {
            return ((LhaFile) archive).size();
        } else if (archive instanceof LhaInputStream) {
            return -1;
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }
}

/* */
