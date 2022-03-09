/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import dorkbox.cabParser.CabException;
import dorkbox.cabParser.CabParser;
import dorkbox.cabParser.CabStreamSaver;
import dorkbox.cabParser.structure.CabEntry;
import dorkbox.cabParser.structure.CabFileEntry;


/**
 * DorkboxCabArchive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040929 nsano initial version <br>
 */
public class DorkboxCabArchive implements Archive {

    /** */
    private CabParser cab;
    /** */
    private InputStream is;
    /** */
    private int size;
    /** */
    private String name;

    /** */
    public DorkboxCabArchive(File file) throws IOException {
        this.size = (int) file.length();
        this.name = file.getName();
        init(new BufferedInputStream(new FileInputStream(file)));
    }

    /** */
    public DorkboxCabArchive(InputStream is) throws IOException {
        this.size = is.available();
        this.name = is.toString();
        init(is);
    }

    /** */
    private final void init(InputStream is) throws IOException {
        this.is = is;
        try {
            this.cab = new CabParser(is, new CabStreamSaver() {
                @Override
                public boolean saveReservedAreaData(byte[] data, int dataLength) {
                    return false;
                }

                @Override
                public OutputStream openOutputStream(CabFileEntry cabFile) {
                    return new ByteArrayOutputStream((int) cabFile.getSize());
                }

                @Override
                public void closeOutputStream(OutputStream outputStream, CabFileEntry cabFile) {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            });
        } catch (CabException e) {
            throw new IOException(e);
        }
    }

    /** */
    public void close() throws IOException {
        is.close();
    }

    /** */
    public Entry[] entries() {
        List<Entry> entries = new ArrayList<>();
        Enumeration<CabEntry> e = cab.entries();
        while (e.hasMoreElements()) {
            entries.add(new DorkboxCabEntry(e.nextElement()));
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    /** */
    public Entry getEntry(String name) {
        Enumeration<CabEntry> e = cab.entries();
        while (e.hasMoreElements()) {
            CabEntry entry = e.nextElement();
            if (entry.getName().equals(name)) {
                return new DorkboxCabEntry(entry);
            }
        }
        return null;
    }

    /** reads a CAB file, parses it, and returns an InputStream representing the named file */
    public InputStream getInputStream(Entry entry) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /** */
    public String getName() {
        return name;
    }

    /** */
    public int size() {
        return size;
    }
}

/* */
