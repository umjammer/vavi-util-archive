/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
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

import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import vavi.io.OutputEngine;
import vavi.io.OutputEngineInputStream;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.WrappedEntry;


/**
 * Represents RAR archived file.
 * warring the library 'com.github.junrar:junrar'
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 220922 nsano initial version <br>
 */
public class JunrarRarArchive implements Archive {

    /** */
    private com.github.junrar.Archive archive;

    /** */
    public JunrarRarArchive(File file) throws IOException {
        try {
            this.archive = new com.github.junrar.Archive(file);
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
            entries[i] = new JunrarRarEntry(headers.get(i));
        }
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        List<FileHeader> headers = archive.getFileHeaders();
        for (FileHeader header : headers) {
            if (header.getFileName().equals(name)) {
                return new JunrarRarEntry(header);
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
                    archive.extractFile((FileHeader) ((WrappedEntry<?>) entry).getWrappedObject(), out);
                } catch (RarException e) {
                    throw new IOException(e);
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
