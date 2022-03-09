/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;

import vavi.io.OutputEngine;
import vavi.io.OutputEngineInputStream;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.WrappedEntry;


/**
 * Represents RAR archived file.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120216 nsano initial version <br>
 */
public class PureJavaRarArchive implements Archive {

    /** */
    private de.innosystec.unrar.Archive archive;

    /** */
    public PureJavaRarArchive(File file) throws IOException {
        try {
            this.archive = new de.innosystec.unrar.Archive(file);
        } catch (RarException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        archive.close();
    }

    @Override
    public Entry[] entries() {
        List<FileHeader> headers = archive.getFileHeaders();
        Entry[] entries = new Entry[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            entries[i] = new PureJavaRarEntry(headers.get(i));
        }
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        List<FileHeader> headers = archive.getFileHeaders();
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getFileNameString().equals(name)) {
                return new PureJavaRarEntry(headers.get(i));
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(final Entry entry) throws IOException {
        return new OutputEngineInputStream(new OutputEngine() {
            private final int BUFFER_SIZE = 4096;
            private InputStream in = new ByteArrayInputStream(new byte[BUFFER_SIZE]);
            private OutputStream out;
            public void initialize(OutputStream out) throws IOException {
                try {
                    archive.extractFile(FileHeader.class.cast(WrappedEntry.class.cast(entry).getWrappedObject()), out);
                } catch (RarException e) {
                    new IOException(e);
                }
            }
            public void execute() throws IOException {
                byte[] buf = new byte[BUFFER_SIZE];
                int l = in.read(buf, 0, BUFFER_SIZE);
                if (l < 0) {
                    out.flush();
                    out.close();
                } else {
                    out.write(buf, 0, l);
                }
            }
            public void finish() throws IOException {
                in.close();
            }
        });
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int size() {
        return archive.getFileHeaders().size();
    }
}

/* */
