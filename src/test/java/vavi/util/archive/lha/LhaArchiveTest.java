/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

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
 * LhaArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/10 umjammer initial version <br>
 */
class LhaArchiveTest {

    @Test
    void test() throws Exception {
        LhaArchive archive = new LhaArchive(LhaArchiveTest.class.getResourceAsStream("/test.lzh"));
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
            c++;
        }
        assertEquals(7, c);
    }

    @Test
    void test2() throws Exception {
        LhaArchive archive = new LhaArchive(new File("src/test/resources/test.lzh"));
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
        Archive archive = new LhaArchive(new File("src/test/resources/test.lzh"));
        Entry entry = archive.entries()[0];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_lha/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    /**
     * @param args 0: lha
     */
    public static void main(String[] args) throws Exception {
        Path target = Paths.get(System.getenv("HOME"), args[0]);
        LhaArchive archive = new LhaArchive(target.toFile());
        Path dir = Paths.get("tmp", "lha-test");
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        for (Entry entry : archive.entries()) {
            Path path = dir.resolve(entry.getName());
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.copy(archive.getInputStream(entry), path);
        }
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        LhaArchive archive = new LhaArchive(new URL("file:src/test/resources/test.lzh").openStream());
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
        }
Debug.println("entries after loop: " + archive.size());
        assertNotEquals(0, archive.size());
        // TODO LhaArchive#getInputStream().available() returns 0 but readable
Debug.println("stream after loop: " + archive.entries()[0].getName() + ", first byte: " + archive.getInputStream(archive.entries()[0]).read());
        assertNotNull(archive.getInputStream(archive.entries()[0]));
        for (Entry entry : archive.entries()) {
            if (!entry.isDirectory() && archive.getInputStream(entry).read() > -1) {
Debug.println("stream after loop: 2nd byte: " + archive.getInputStream(entry).read());
                return;
            }
        }
        fail("no file size > 0");
    }
}

/* */
