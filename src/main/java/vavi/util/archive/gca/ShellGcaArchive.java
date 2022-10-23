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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.CommonEntry;
import vavi.util.archive.Entry;


/**
 * GCA アーカイブを処理するサービスプロバイダです．
 *
 * TODO ファイル名が長い時最初 ... になってフォーマットが崩れる
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030128 nsano initial version <br>
 *          0.01 030228 nsano refine <br>
 */
public class ShellGcaArchive implements Archive {

    /** */
    private List<CommonEntry> entries = new ArrayList<>();

    /** */
    private File file;

    /** */
    public ShellGcaArchive(File file) throws IOException {

        this.file = file;

        final DateFormat sdf =
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
Debug.println(entry);

                entries.add(entry);
            } catch (Exception e) {
Debug.println(e);
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

    //----

    /** */
    public static void main(String[] args) throws Exception {
        new ShellGcaArchive(new File(args[0]));
    }
}

/* */
