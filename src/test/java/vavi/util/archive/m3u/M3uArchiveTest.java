/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.m3u;

import java.io.BufferedInputStream;
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
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * M3uArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2026-09-22 nsano initial version <br>
 */
class M3uArchiveTest {

    @Test
    @DisplayName("path")
    void test1() throws Exception {
        Archive archive = new M3uArchive(new File("src/test/resources/test.m3u"));
        assertEquals(2, archive.size());
        assertEquals("duke_trunk.png", archive.entries()[0].getName());
        assertEquals("Duke Trunk", archive.entries()[0].getComment());
        assertEquals("test.zip", archive.entries()[1].getName());
        assertEquals("Test Zip", archive.entries()[1].getComment());
    }

    @Test
    @DisplayName("byte range")
    void test2() throws Exception {
        Archive archive = new M3uArchive(new File("src/test/resources/test.m3u8"));
        assertEquals(2, archive.size());

        Entry entry0 = archive.entries()[0];
        assertEquals(100, entry0.getSize());
        try (InputStream is = archive.getInputStream(entry0)) {
            byte[] bytes = is.readAllBytes();
            assertEquals(100, bytes.length);
        }

        Entry entry1 = archive.entries()[1];
        assertEquals(200, entry1.getSize());
        try (InputStream is = archive.getInputStream(entry1)) {
            byte[] bytes = is.readAllBytes();
            assertEquals(200, bytes.length);
        }
    }

    @Test
    @DisplayName("spi file")
    void test3() throws Exception {
        Archive archive = Archives.getArchive(new File("src/test/resources/test.m3u"));
        assertInstanceOf(M3uArchive.class, archive);
        assertEquals(2, archive.size());
    }

    @Test
    @DisplayName("spi stream")
    void test31() throws Exception {
        Path path = Paths.get("src/test/resources/test.m3u");
        Archive archive = Archives.getArchive(new BufferedInputStream(Files.newInputStream(path)));
        assertInstanceOf(M3uArchive.class, archive);
        assertEquals(2, archive.size());
    }

    @Test
    @DisplayName("extract")
    void test4() throws Exception {
        Archive archive = new M3uArchive(new File("src/test/resources/test.m3u"));
        Entry entry = archive.entries()[0];
        InputStream is = archive.getInputStream(entry);
        Path out = Paths.get("tmp/out_m3u/" + entry.getName());
        Files.createDirectories(out.getParent());
        Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
        assertEquals(Files.size(Paths.get("src/test/resources/duke_trunk.png")), Files.size(out));
    }

    @Test
    @DisplayName("multivariant playlist")
    void test5() throws Exception {
        String content = """
                #EXTM3U
                #EXT-X-STREAM-INF:BANDWIDTH=1280000,RESOLUTION=720x480
                low.m3u8
                #EXT-X-STREAM-INF:BANDWIDTH=2560000,RESOLUTION=1280x720
                high.m3u8
                """;
        Path temp = Files.createTempFile("test_mv", ".m3u8");
        Files.writeString(temp, content);
        try {
            Archive archive = new M3uArchive(temp.toFile());
            assertEquals(2, archive.size());
            assertEquals("low.m3u8", archive.entries()[0].getName());
            assertEquals("720x480", archive.entries()[0].getComment());
            assertEquals("high.m3u8", archive.entries()[1].getName());
            assertEquals("1280x720", archive.entries()[1].getComment());
        } finally {
            Files.deleteIfExists(temp);
        }
    }

    @Test
    @DisplayName("simple m3u without targetduration")
    void test6() throws Exception {
        String content = """
                #EXTM3U
                #EXTINF:180,Artist - Title
                song1.mp3
                #EXTINF:200,Artist - Title 2
                song2.mp3
                """;
        Path temp = Files.createTempFile("test_simple", ".m3u");
        Files.writeString(temp, content);
        try {
            Archive archive = new M3uArchive(temp.toFile());
            assertEquals(2, archive.size());
            assertEquals("song1.mp3", archive.entries()[0].getName());
            assertEquals("Artist - Title", archive.entries()[0].getComment());
            assertEquals("song2.mp3", archive.entries()[1].getName());
            assertEquals("Artist - Title 2", archive.entries()[1].getComment());
        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
