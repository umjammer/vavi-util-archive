/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;
import vavi.util.archive.WrappedEntry;


/**
 * Represents RAR archived file.
 * wrapping the library 'com.github.junrar:junrar'
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 220922 nsano initial version <br>
 */
public class JunrarRarArchive extends InputStreamSupport implements Archive {

    /** */
    private com.github.junrar.Archive archive;

    /** */
    private String name;

    /** */
    private Entry[] entries;

    /** */
    public JunrarRarArchive(File file) throws IOException {
        try {
            this.archive = new com.github.junrar.Archive(file);
            this.name = file.getPath();
        } catch (RarException e) {
            throw new IOException(e);
        }
    }

    /** */
    public JunrarRarArchive(InputStream is) throws IOException {
        super(is);
        try {
            this.archive = new com.github.junrar.Archive(archiveFileForInputStream);
            this.name = archiveFileForInputStream.getPath();
        } catch (com.github.junrar.exception.RarException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        archive.close();
    }

    @Override
    public Entry[] entries() {
        if (entries == null) {
            List<FileHeader> headers = archive.getFileHeaders();
            entries = new Entry[headers.size()];
            for (int i = 0; i < headers.size(); i++) {
                entries[i] = new JunrarRarEntry(headers.get(i));
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
    public InputStream getInputStream(final Entry entry) throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            archive.extractFile((FileHeader) ((WrappedEntry<?>) entry).getWrappedObject(), baos);
            return new ByteArrayInputStream(Arrays.copyOfRange(baos.toByteArray(), 0, (int) entry.getSize()));
        } catch (RarException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int size() {
        return entries().length;
    }
}

/* */
