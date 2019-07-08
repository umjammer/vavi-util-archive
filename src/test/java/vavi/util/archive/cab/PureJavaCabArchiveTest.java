/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Archive;
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * PureJavaCabArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/04/08 umjammer initial version <br>
 */
class PureJavaCabArchiveTest {

    @Test
    void test() throws Exception {
        Archive archive = new PureJavaCabArchive(new File("tmp/dajava.cab"));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(true);
    }

    @Test
    void test2() throws Exception {
        Archive archive = Archives.getArchive(new File("tmp/dajava.cab"));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(true);
    }
}

/* */
