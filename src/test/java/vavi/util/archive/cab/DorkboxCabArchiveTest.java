/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * DorkboxCabArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/14 umjammer initial version <br>
 */
class DorkboxCabArchiveTest {

    @Test
    void test() throws Exception {
        Archive archive = new DorkboxCabArchive(DorkboxCabArchiveTest.class.getResourceAsStream("/test.cab"));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(true);
    }


    @Test
    @DisplayName("extract")
    public void test3() throws Exception {
        Archive archive = new DorkboxCabArchive(new File("src/test/resources/test.cab"));
        Entry entry = archive.entries()[0];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_dorkboxcab/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        DorkboxCabArchive archive = new DorkboxCabArchive(new URL("file:src/test/resources/test.cab").openStream());
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
        }
Debug.println("entries after loop: " + archive.entries().length);
        assertNotEquals(0, archive.entries().length);
Debug.println("stream after loop: " + archive.entries()[0].getName() + ", " + archive.getInputStream(archive.entries()[0]).available());
        assertNotNull(archive.getInputStream(archive.entries()[0]));
        for (Entry entry : archive.entries()) {
            if (!entry.isDirectory() && archive.getInputStream(entry).available() > 0) {
Debug.println("stream after loop: " + archive.getInputStream(entry).available());
                return;
            }
        }
        fail("no file size > 0");
    }
}
