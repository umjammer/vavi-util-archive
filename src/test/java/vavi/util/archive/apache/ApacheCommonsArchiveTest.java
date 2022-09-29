/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.apache;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.cab.DorkboxCabArchive;
import vavi.util.archive.rar.JunrarRarArchive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * ApacheCommonsArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022/03/09 nsano initial version <br>
 */
class ApacheCommonsArchiveTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "/test.zip",
        "/test.arj", // TODO seems not work all
    })
    void test(String name) throws Exception {
        Archive archive = new ApacheCommonsArchive(ApacheCommonsArchiveTest.class.getResourceAsStream(name));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(archive.entries().length > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/test.zip",
            "/test.arj", // TODO seems not work all
    })
    void test2(String name) throws Exception {
        Archive archive = new ApacheCommonsArchive(Paths.get("src/test/resources", name).toFile());
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(archive.entries().length > 0);
    }

    @Test
    void test1() throws Exception {
        ApacheCommonsArchiveSpi spi = new ApacheCommonsArchiveSpi();
        String[] suffixes = spi.getFileSuffixes();
Debug.println(Arrays.toString(suffixes));
        assertTrue(Arrays.asList(suffixes).contains("xz"));
        assertTrue(Arrays.asList(suffixes).contains("jar"));
        assertTrue(Arrays.asList(suffixes).contains("zst"));
        assertTrue(Arrays.asList(suffixes).contains("lzma"));
        assertFalse(Arrays.asList(suffixes).contains("Z")); // TODO just check the logic, not semantics (means .Z may be included by some compression type)
    }

    @Test
    @DisplayName("extract")
    public void test3() throws Exception {
        Archive archive = new ApacheCommonsArchive(new File("src/test/resources/test.zip"));
        Entry entry = archive.entries()[2];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_apache/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        ApacheCommonsArchive archive = new ApacheCommonsArchive(new URL("file:src/test/resources/test.zip").openStream());
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName() + ", " + entry.isDirectory());
        }
Debug.println("entries after loop: " + archive.size());
        assertNotEquals(0, archive.size());
        // TODO ApacheCommonsArchive returns directory
Debug.println("stream after loop: " + archive.entries()[1].getName() + ", firstByte: " + archive.getInputStream(archive.entries()[1]).read());
        assertNotNull(archive.getInputStream(archive.entries()[0]));
        for (Entry entry : archive.entries()) {
            if (!entry.isDirectory()) {
                // TODO ApacheCommonsArchive#getInputStream().available() returns 0 but readable
                InputStream is = archive.getInputStream(entry);
                int firstByte = is.read();
                if (firstByte > -1) {
Debug.println("stream after loop: firstByte: " + firstByte);
                    return;
                }
            }
        }
        fail("no file size > 0");
    }
}

/* */
