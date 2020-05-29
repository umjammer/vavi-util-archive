/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * NativeRarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
class NativeRarArchiveTest {

    @Test
    void test() {
        fail("Not yet implemented");
    }

    // for Native method ------------------------------------------------------

    /** */
    public static void main(String[] args) throws IOException {
        NativeRarArchive rar = new NativeRarArchive(new File(args[0])) {
            {
                System.err.println("rar: " + getVersion());
            }
        };
//System.err.println("handle: " + gca.instance);
//System.err.println("size: " + gca.getSelectedSize());
//System.err.println("csize: " + gca.getSelectedCompressedSize());
//System.err.println("ratio: " + gca.getSelectedRatio());
        Entry<?> entry = rar.getEntry(args[1]);
        InputStream is = rar.getInputStream(entry);
System.err.println("is: " + is);
        System.exit(0);
    }
}

/* */
