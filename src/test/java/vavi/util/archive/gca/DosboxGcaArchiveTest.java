/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.gca;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import vavi.util.archive.Archive;
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * DosboxGcaArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 260822 nsano initial version <br>
 */
@EnabledIf("gcacExists")
class DosboxGcaArchiveTest {

    static final String file = "src/test/resources/test.gca";

    /** there is nothing for this provider to do without the win32 program it runs */
    static boolean gcacExists() {
        return DosboxGcaArchiveSpi.isAvailable();
    }

    @Test
    @DisplayName("the listing gcac gives, with the names it abbreviates put back")
    void test1() throws Exception {
        Archive archive = new DosboxGcaArchive(new File(file));
        try {
            Entry[] entries = archive.entries();
            assertEquals(6, entries.length);
            assertEquals(6, archive.size());
            assertEquals(file, archive.getName());

            assertEquals(Arrays.asList("tmp/EntryTableModel.java",
                                       "tmp/JWinZip.java",
                                       "tmp/manifest.mf",
                                       "tmp/JWinZipResource_ja.properties",
                                       "tmp/ja.properties",
                                       "tmp/build.xml"),
                         Arrays.stream(entries).map(Entry::getName).toList());

            // the listing abbreviates this one to "...nZipResource_ja.properties"
            Entry entry = archive.getEntry("tmp/JWinZipResource_ja.properties");
            assertNotNull(entry);
            assertEquals(6017, entry.getSize());
            assertEquals("2002/11/25 14:14:04",
                         new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(entry.getTime()));
        } finally {
            archive.close();
        }
    }

    @Test
    @DisplayName("what the entries are made of")
    void test2() throws Exception {
        Archive archive = new DosboxGcaArchive(new File(file));
        try {
            Entry entry = archive.getEntry("tmp/build.xml");
            assertNotNull(entry);
            try (InputStream is = archive.getInputStream(entry)) {
                String text = new String(is.readAllBytes(), Charset.forName("windows-31j"));
                assertEquals(entry.getSize(), text.getBytes(Charset.forName("windows-31j")).length);
                assertTrue(text.startsWith("<?xml version=\"1.0\" encoding=\"shift_jis\"?>"));
            }
        } finally {
            archive.close();
        }
    }

    @Test
    @DisplayName("the spi picks a gca up")
    void test3() throws Exception {
        Archive archive = Archives.getArchive(new File(file));
        try {
            assertInstanceOf(DosboxGcaArchive.class, archive);
            assertEquals(6, archive.size());
        } finally {
            archive.close();
        }
    }
}
