/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.stuffit;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * NativeStuffItArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
class NativeStuffItArchiveTest {

    @Test
    @Disabled
    void test() {
        fail("Not yet implemented");
    }

    // for Native method ------------------------------------------------------

    /** */
    public static void main(String[] args) throws IOException {
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
        for (Entry<?> entry : sit.entries()) {
            System.err.println("entry: " + entry);
        }
        System.exit(0);
    }
}

/* */
