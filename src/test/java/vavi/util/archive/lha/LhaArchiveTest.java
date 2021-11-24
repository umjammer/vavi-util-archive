/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;


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

    /**
     * @param args
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
}

/* */
