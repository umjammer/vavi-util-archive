/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * ZipArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-11 nsano initial version <br>
 */
//@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
class ZipArchiveTest {

    static {
        System.setProperty("vavi.util.logging.VaviFormatter.extraClassMethod", "sun\\.util\\.logging\\..*?#.*");
    }

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "archive.zip.file1")
    String file1 = "src/test/resources/avif.zip";

    @Property(name = "archive.zip.file2")
    String file2 = "src/test/resources/ms932.zip";

    @Property(name = "archive.zip.fileN")
    String fileN = "file:src/test/resources/avif.zip";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    @DisplayName("path")
    void test1() throws Exception {
        Path path = Paths.get(file1);
        Archive archive = new ZipArchive(path.toFile());
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
            c++;
        }
//        assertEquals(7, c);
    }

    @Test
    @DisplayName("path, encoding")
    void test2() throws Exception {
        System.setProperty(ZipArchive.ZIP_ENCODING, "ms932");
        Path path = Paths.get(file2);
        Archive archive = new ZipArchive(path.toFile());
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
            c++;
        }
//        assertEquals(7, c);
    }

    @Test
    @DisplayName("path, different encoding")
    void test3() throws Exception {
        System.setProperty(ZipArchive.ZIP_ENCODING, "utf-8");
        Path path = Paths.get(file2);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            Archive archive = new ZipArchive(path.toFile());
            for (Entry entry : archive.entries()) {
                System.out.println(entry.getName());
            }
        });
Debug.println("exception cause: " + e.getMessage());
        assertEquals("MALFORMED", e.getMessage());
    }

    @Test
    @DisplayName("path, failsafe encoding")
    void test4() throws Exception {
        System.setProperty(ZipArchive.ZIP_ENCODING, "utf-8");
        Path path = Paths.get(file2);
        Archive archive = new ZipArchive(path.toFile(), "ms932");
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
        }
    }

    /** until Predicate#not release */
    static boolean isNotDir(Path path) {
        return !Files.isDirectory(path);
    }

    /** make filename ms932 encoded zip */
    public static void main(String[] args) throws IOException {
        Charset charset = Charset.forName("MS932");

        Path out = Paths.get("src/test/resources/ms932.zip");
        OutputStream os = new BufferedOutputStream(Files.newOutputStream(out));
        java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(os, charset);

        Path dir = Paths.get("tmp/zip");
        Files.walk(dir).filter(ZipArchiveTest::isNotDir).forEach(p -> {
            try {
Debug.println("zipping: " + dir.relativize(p));
                java.util.zip.ZipEntry ze = new java.util.zip.ZipEntry(dir.relativize(p).toString());
                zos.putNextEntry(ze);
                zos.write(Files.readAllBytes(p));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        zos.close();
        os.close();
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        Archive archive = new ZipArchive(new URL(fileN).openStream());
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
        }
Debug.println("entries after loop: " + archive.entries().length);
        assertNotEquals(0, archive.entries().length);
Debug.println("stream after loop: " + archive.getInputStream(archive.entries()[0]));
        assertNotNull(archive.getInputStream(archive.entries()[0]));
        for (Entry entry : archive.entries()) {
            if (!entry.isDirectory() && archive.getInputStream(entry).available() > 0) {
Debug.println("stream after loop: " + archive.getInputStream(entry).available());
                break;
            }
        }
    }
}
