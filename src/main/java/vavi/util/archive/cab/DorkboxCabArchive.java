/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dorkbox.cabParser.CabException;
import dorkbox.cabParser.CabParser;
import dorkbox.cabParser.CabStreamSaver;
import dorkbox.cabParser.structure.CabEntry;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.InputStreamSupport;


/**
 * Represents CAB archive using a library Dorkbox.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040929 nsano initial version <br>
 */
public class DorkboxCabArchive extends InputStreamSupport implements Archive {

    /** */
    private final CabParser cab;

    /** */
    private final String name;

    /** */
    private Entry[] entries;

    /** */
    public DorkboxCabArchive(File file) throws IOException {
        try {
            this.cab = new CabParser(file, (CabStreamSaver) null);
        } catch (CabException e) {
            throw new IOException(e);
        }
        this.name = file.getName();
    }

    /** */
    public DorkboxCabArchive(InputStream is) throws IOException {
        super(is);
        try {
            this.cab = new CabParser(archiveFileForInputStream, (CabStreamSaver) null);
        } catch (CabException e) {
            throw new IOException(e);
        }
        this.name = archiveFileForInputStream.getName();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public Entry[] entries() {
        if (entries == null) {
            List<Entry> entries = new ArrayList<>();
            for (CabEntry e : cab.entries()) {
                entries.add(new DorkboxCabEntry(e));
            }
            this.entries = entries.toArray(new Entry[0]);
        }
        return this.entries;
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry e : entries()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(Entry entry) {
        try {
            return cab.getInputStream(((DorkboxCabEntry) entry).getWrappedObject());
        } catch (CabException | IOException e) {
            throw new IllegalStateException(e);
        }
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
