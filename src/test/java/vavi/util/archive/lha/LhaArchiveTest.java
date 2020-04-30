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

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * LhaArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/10 umjammer initial version <br>
 */
class LhaArchiveTest {

    @Test
    void test() throws Exception {
        LhaArchive archive = new LhaArchive(new File("tmp/ugca010c.lzh"));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(true);
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
