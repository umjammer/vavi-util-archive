/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * PureJavaRarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/02/16 umjammer initial version <br>
 */
public class PureJavaRarArchiveTest {

    @Test
    public void test() throws Exception {
        Archive archive = new PureJavaRarArchive(new File("src/test/resources/test.rar"));
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName() + "\t" + LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getTime()), ZoneId.systemDefault()));
            c++;
        }
        assertEquals(6, c);
    }

    @Test
    @Disabled("java-unrar accept bad jar header for corrupt rar file extraction, rar5 also")
    public void test2() throws Exception {
        Exception e = assertThrows(IOException.class, () -> {
            new PureJavaRarArchive(new File("src/test/resources/rar5.rar"));
        });
        assertInstanceOf(de.innosystec.unrar.exception.RarException.class, e.getCause());
    }
}

/* */
