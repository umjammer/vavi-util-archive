/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

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
import static org.junit.jupiter.api.Assertions.fail;


/**
 * AntZipArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/14 umjammer initial version <br>
 */
class AntZipArchiveTest {

    @Test
    void test() throws Exception {
        Archive archive = new AntZipArchive(new File("src/test/resources/test.zip"));
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
            c++;
        }
        assertEquals(7, c);
    }

    @Test
    @DisplayName("extract")
    public void test3() throws Exception {
        Archive archive = new AntZipArchive(new File("src/test/resources/test.zip"));
        // TODO AntZipArchive#entries() contains directory (this [0] is dir)
        Entry entry = archive.entries()[1];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_antzip/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        Archive archive = new AntZipArchive(new URL("file:src/test/resources/test.zip").openStream());
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
        }
Debug.println("entries after loop: " + archive.size());
        assertNotEquals(0, archive.size());
        // TODO AntZipArchive#entries() contains directory (this [0] is dir)
Debug.println("stream after loop: " + archive.entries()[1].getName() + ", firstByte: " + archive.getInputStream(archive.entries()[1]).read());
        assertNotNull(archive.getInputStream(archive.entries()[0]));
        for (Entry entry : archive.entries()) {
            if (!entry.isDirectory() && archive.getInputStream(entry).read() > -1) {
Debug.println("stream after loop: 2ndByte: " + archive.getInputStream(entry).read());
                return;
            }
        }
        fail("no file size > 0");
    }
}
