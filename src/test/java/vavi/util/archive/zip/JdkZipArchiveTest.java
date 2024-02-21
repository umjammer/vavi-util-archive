/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * JdkZipArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-11 nsano initial version <br>
 */
//@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
class JdkZipArchiveTest {

    static {
        System.setProperty("vavi.util.logging.VaviFormatter.extraClassMethod", "sun\\.util\\.logging\\..*?#.*");
    }

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "archive.zip.file1")
    String file1 = "src/test/resources/avif.zip";

    @Property(name = "archive.zip.file2")
    String file932 = "src/test/resources/ms932.zip";

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
        Archive archive = new JdkZipArchive(path.toFile());
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
        System.setProperty(JdkZipArchive.ZIP_ENCODING, "ms932");
        Path path = Paths.get(file932);
        Archive archive = new JdkZipArchive(path.toFile());
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
            c++;
        }
//        assertEquals(7, c);
    }

    @Test
    @DisplayName("path, different encoding")
    void test22() throws Exception {
        System.setProperty(JdkZipArchive.ZIP_ENCODING, "utf-8");
        Path path = Paths.get(file932);
        IOException e = assertThrows(IOException.class, () -> {
            Archive archive = new JdkZipArchive(path.toFile());
            for (Entry entry : archive.entries()) {
                System.out.println(entry.getName());
            }
        });
Debug.println("exception cause: " + e.getMessage());
        assertInstanceOf(ZipException.class, e.getCause());
        assertTrue(e.getCause().getMessage().contains("invalid CEN header"));
    }

    @Test
    @DisplayName("path, failsafe encoding")
    void test23() throws Exception {
        System.setProperty(JdkZipArchive.ZIP_ENCODING, "utf-8");
        Path path = Paths.get(file932);
        Archive archive = new JdkZipArchive(path.toFile(), "ms932");
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
        }
    }

    @Test
    @DisplayName("spi, failsafe encoding")
    void test24() throws Exception {
        System.setProperty(JdkZipArchive.ZIP_ENCODING, "utf-8");
        Path path = Paths.get("src/test/resources/ms932.zip");
//        Path path = Paths.get(file932);
        Map<String, Object> env = new HashMap<>();
        env.put(JdkZipArchiveSpi.ENV_KEY_FAILSAFE_ENCODING, "ms932");
        Archive archive = Archives.getArchive(path.toFile(), env);
        assertInstanceOf(JdkZipArchive.class, archive);
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName());
        }
    }

    @Test
    @DisplayName("spi, failsafe encoding, not set")
    void test25() throws Exception {
        System.setProperty(JdkZipArchive.ZIP_ENCODING, "utf-8");
        Path path = Paths.get(file932);
        IOException e = assertThrows(IOException.class, () -> Archives.getArchive(path.toFile()));
Debug.println("exception cause: " + e.getMessage());
        assertInstanceOf(ZipException.class, e.getCause());
        assertTrue(e.getCause().getMessage().contains("invalid CEN header"));
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
        Files.walk(dir).filter(JdkZipArchiveTest::isNotDir).forEach(p -> {
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
    @DisplayName("extract")
    public void test3() throws Exception {
        Archive archive = new JdkZipArchive(new File("src/test/resources/test.zip"));
        // TODO JdkZipArchive#entries() contains directory (this [0] is dir)
        Entry entry = archive.entries()[1];
Debug.println(entry.getName() + ", " + entry.getSize());
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_jdkzip/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(out), entry.getSize());
    }

    @Test
    @DisplayName("extract all")
    public void test31() throws Exception {
        Path inZip = Paths.get("src/test/resources/test.zip");
        Path outDir = Paths.get("tmp/out_31");
        Archive archive = Archives.getArchive(inZip.toFile());
        for (Entry entry : Arrays.stream(archive.entries()).filter(e -> !e.isDirectory()).toList()) {
            Path out = outDir.resolve(entry.getName());
            Files.createDirectories(out.getParent());
            Files.copy(archive.getInputStream(entry), out, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        Archive archive = new JdkZipArchive(new URL(fileN).openStream());
        for (Entry entry : archive.entries()) {
            System.out.println(entry.getName() + ", " + entry.getSize());
        }
Debug.println("entries after loop: " + archive.size());
        assertNotEquals(0, archive.size());
        // JdkZipArchive#getInputStream() accept directory
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
