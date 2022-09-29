/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;
import vavi.util.archive.rar.PureJavaRarArchive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * ApacheSevenZipArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
class ApacheSevenZipArchiveTest {

    @Test
    void test() throws Exception {
        ApacheSevenZipArchive sevenZip = new ApacheSevenZipArchive(new File("src/test/resources/test.7z"));
        assertEquals(20, sevenZip.entries().length);
    }

    @Test
    @Disabled("apache compress 7z accept only 7z?")
    public void test2() throws Exception {
        Archive archive = new ApacheSevenZipArchive(new File("src/test/resources/test.rar"));
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName() + "\t" + LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getTime()), ZoneId.systemDefault()));
            c++;
        }
        assertEquals(6, c);
    }


    @Test
    @DisplayName("inputStream")
    void test5() throws Exception {
        ApacheSevenZipArchive archive = new ApacheSevenZipArchive(new URL("file:src/test/resources/test.7z").openStream());
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
                break;
            }
        }
    }
}

/* */
