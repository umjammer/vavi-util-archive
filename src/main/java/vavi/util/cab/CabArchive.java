/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.cab;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.spi.CommonEntry;


/**
 * CabArchive.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040929 nsano initial version <br>
 */
public class CabArchive implements Archive {

    /** */
    private Cab cab;
    /** */
    private InputStream is;
    /** */
    private File file;

    /** */
    private List<Entry> entries = new ArrayList<>();

    /** */
    public CabArchive(String fileName) throws IOException {
        this.file = new File(fileName);
        this.is = new FileInputStream(file);
        this.cab = new Cab(is, 1);

        for (CabFolder folder : cab.getFolders()) {
            for (CabFile file : folder.getFiles()) {
                CommonEntry entry = new CommonEntry();
                entry.setName(folder.toString() + File.separator + file.getFileName());
                // TODO entry.set...
                entries.add(entry);
            }
        }
    }
    
    /** */
    public void close() throws IOException {
        is.close();
    }

    /** */
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    /** */
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
          if (entry.getName().equals(name)) {
                return entry;
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
        return file.getName();
    }

    /** */
    public int size() {
        return (int) file.length();
    }
}

/* */
