/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
}

/* */
