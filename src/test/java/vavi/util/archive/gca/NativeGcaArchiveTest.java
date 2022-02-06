/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.gca;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import vavi.util.archive.Entry;


/**
 * NativeGcaArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
class NativeGcaArchiveTest {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void test() throws IOException {
        main(new String[] { "src/test/resources/test.gca" });
    }

    // ----

    /** */
    public static void main(String[] args) throws IOException {
        NativeGcaArchive gca = new NativeGcaArchive(new File(args[0])) {
            {
System.err.println("gca: " + getVersion());
            }
        };
//System.err.println("handle: " + gca.instance);
//System.err.println("size: " + gca.getSelectedSize());
//System.err.println("csize: " + gca.getSelectedCompressedSize());
//System.err.println("ratio: " + gca.getSelectedRatio());
        Entry entry = gca.getEntry(args[1]);
        InputStream is = gca.getInputStream(entry);
System.err.println("is: " + is);
    }
}

/* */
