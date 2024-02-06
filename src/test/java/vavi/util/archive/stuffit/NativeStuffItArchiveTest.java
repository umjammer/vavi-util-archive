/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.stuffit;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import vavi.util.archive.Entry;


/**
 * NativeStuffItArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
@EnabledOnOs(OS.WINDOWS)
class NativeStuffItArchiveTest {

    @Test
    void test() throws Exception {
        main(new String[] { "src/test/resources/test.sit" });
    }

    // ----

    /** */
    public static void main(String[] args) throws Exception {
        NativeStuffItArchive sit = new NativeStuffItArchive(new File(args[0])) {{
            System.err.println("sit: " + getVersion());
        }};
//System.err.println("handle: " + sit.instance);
//System.err.println("size: " + sit.getSelectedSize());
//System.err.println("csize: " + sit.getSelectedCompressedSize());
//System.err.println("ratio: " + sit.getSelectedRatio());
//        Entry entry = sit.getEntry(args[1]);
//        InputStream is = sit.getInputStream(entry);
//System.err.println("is: " + is);
        for (Entry entry : sit.entries()) {
            System.err.println("entry: " + entry);
        }
    }
}

/* */
