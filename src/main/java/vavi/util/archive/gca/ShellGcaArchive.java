/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.gca;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import vavi.util.archive.Archive;
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;

import static java.lang.System.getLogger;


/**
 * ShellGcaArchive represents GCA archive using shell output.
 *
 * TODO when an entry name is long, it cannot format well because of abbreviation "...".
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030128 nsano initial version <br>
 *          0.01 030228 nsano refine <br>
 */
public class ShellGcaArchive implements Archive {

    private static final Logger logger = getLogger(ShellGcaArchive.class.getName());

    /** */
    private final List<CommonEntry> entries = new ArrayList<>();

    /** */
    private final File file;

    /** */
    public ShellGcaArchive(File file) throws IOException {

        this.file = file;

        DateFormat sdf =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Process p = Runtime.getRuntime().exec(new String[] { "gcac", "l", file.toString() });
        BufferedReader r = new BufferedReader(
            new InputStreamReader(p.getInputStream()));
        r.readLine();
        r.readLine();
        r.readLine();
        while (r.ready()) {
            String s = r.readLine();

            try {
                CommonEntry entry = new CommonEntry();

                StringTokenizer st = new StringTokenizer(s, " \t");
                while (st.hasMoreTokens()) {
                    entry.setName(st.nextToken());
                    entry.setTime(
                        sdf.parse(st.nextToken() + " " +
                                  st.nextToken()).getTime());
                    entry.setSize(Long.parseLong(st.nextToken()));
                    entry.setCompressedSize(Long.parseLong(st.nextToken()));
                    @SuppressWarnings("unused")
                    String ratio = st.nextToken();
                }
logger.log(Level.DEBUG, entry);

                entries.add(entry);
            } catch (Exception e) {
logger.log(Level.ERROR, e.getMessage(), e);
            }
        }
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
        return file.getPath();
    }

    @Override
    public int size() {
        return entries.size();
    }

    // ----

    /** */
    public static void main(String[] args) throws Exception {
        new ShellGcaArchive(new File(args[0]));
    }
}
