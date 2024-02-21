/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;


/**
 * ShellRarArchive represents a RAR archive using shell outoput.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021105 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class ShellRarArchive implements Archive {

    /** */
    private List<CommonEntry> entries = new ArrayList<>();

    /** */
    public ShellRarArchive(File file) throws IOException {
        DateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
        Process p = Runtime.getRuntime().exec(new String[] { "unrar", "v", file.toString() });
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        r.readLine();
        r.readLine();
        r.readLine();
        r.readLine();
        r.readLine();
        r.readLine();
        r.readLine();
        r.readLine();
        while (r.ready()) {
            String n = r.readLine();
            String s = r.readLine();
            if (n.startsWith("--------")) {
                break;
            }

            try {
                CommonEntry entry = new CommonEntry();

                entry.setName(n.trim());

                StringTokenizer st = new StringTokenizer(s, " \t");
                while (st.hasMoreTokens()) {
                    entry.setSize(Long.parseLong(st.nextToken()));
                    entry.setCompressedSize(Long.parseLong(st.nextToken()));
                    @SuppressWarnings("unused")
                    String ratio = st.nextToken();
                    entry.setTime(sdf.parse(st.nextToken() + " " + st.nextToken()).getTime());
                    @SuppressWarnings("unused")
                    String attribute = st.nextToken();
                    entry.setCrc(Long.parseLong(st.nextToken(), 16));
                    entry.setMethod(getMethodNumber(st.nextToken()));
                    @SuppressWarnings("unused")
                    String version = st.nextToken();
                }
Debug.println(entry);

                entries.add(entry);
            } catch (Exception e) {
Debug.println(e);
            }
        }
    }

    /** */
    private static final String[] methods = { "m3e" };

    /** */
    private static int getMethodNumber(String method) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equals(method)) {
                return i;
            }
        }

        throw new NoSuchElementException(method);
    }

    @Override
    public void close() {
    }

    @Override
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    @Override
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public InputStream getInputStream(Entry entry) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}

/* */
