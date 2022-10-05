/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * JunrarRarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/09/22 umjammer initial version <br>
 */
public class JunrarRarArchiveTest {

    static {
        System.setProperty("vavi.util.logging.VaviFormatter.extraClassMethod", "org\\.slf4j\\.impl\\.JDK14LoggerAdapter#(log|warn)");
    }

    @Test
    public void test() throws Exception {
        Archive archive = new JunrarRarArchive(new File("src/test/resources/test.rar"));
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName() + "\t" + LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getTime()), ZoneId.systemDefault()));
            c++;
        }
        assertEquals(6, c);
    }

    @Test
    @DisplayName("junrar doesn't support rar5")
    public void test2() throws Exception {
        Exception e = assertThrows(IOException.class, () -> new JunrarRarArchive(new File("src/test/resources/rar5.rar")));
        assertInstanceOf(com.github.junrar.exception.UnsupportedRarV5Exception.class, e.getCause());
    }

    @Test
    @DisplayName("extract")
    public void test3() throws Exception {
        Archive archive = new JunrarRarArchive(new File("src/test/resources/test.rar"));
        Entry entry = archive.entries()[0];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_junrar/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        JunrarRarArchive archive = new JunrarRarArchive(new URL("file:src/test/resources/test.rar").openStream());
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

/* */
