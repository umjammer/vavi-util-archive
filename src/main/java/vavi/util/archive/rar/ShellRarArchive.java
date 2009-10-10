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
import vavi.util.archive.Entry;
import vavi.util.archive.spi.CommonEntry;


/**
 * RAR �A�[�J�C�u����������T�[�r�X�v���o�C�_�ł��D
 * (�O���V�F���N���o�[�W����)
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021105 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class ShellRarArchive implements Archive {

    /** */
    private List<CommonEntry> entries = new ArrayList<CommonEntry>();

    /** */
    public ShellRarArchive(File file) throws IOException {
        final DateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
        Process p = Runtime.getRuntime().exec("unrar v " + file);
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
    private int getMethodNumber(String method) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equals(method)) {
                return i;
            }
        }

        throw new NoSuchElementException(method);
    }

    /**
     * �t�@�C������܂��B
     */
    public void close() throws IOException {
    }

    /**
     * �t�@�C���G���g���̗񋓂�Ԃ��܂��B
     */
    public Entry[] entries() {
        Entry[] entries = new Entry[this.entries.size()];
        this.entries.toArray(entries);
        return entries;
    }

    /**
     * �w�肳�ꂽ���O�� RAR �t�@�C���G���g����Ԃ��܂��B
     * ������Ȃ��ꍇ�� null ��Ԃ��܂��B
     */
    public Entry getEntry(String name) {
        for (Entry entry : entries) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * �w�肳�ꂽ �t�@�C���G���g���̓��e��ǂݍ��ނ��߂̓��̓X�g���[����
     * �Ԃ��܂��B
     */
    public InputStream getInputStream(Entry entry) throws IOException {
        return null;
    }

    /**
     * �t�@�C���̃p�X����Ԃ��܂��B
     */
    public String getName() {
        return null;
    }

    /**
     * �t�@�C�����̃G���g���̐���Ԃ��܂��B
     */
    public int size() {
        return 0;
    }

    //----
    
    /** */
    public static void main(String[] args) throws Exception {
        new ShellRarArchive(new File(args[0]));
    }
}

/* */
