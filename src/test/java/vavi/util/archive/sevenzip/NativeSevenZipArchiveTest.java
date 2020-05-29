/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * NativeSevenZipArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
class NativeSevenZipArchiveTest {

    @Test
    void test() {
        fail("Not yet implemented");
    }

    // for Native method ------------------------------------------------------

    /** java NativeSevenZipArchive sevenZipFile oneOfEntry */
    public static void main(String[] args) throws IOException {
        NativeSevenZipArchive sevenZip = new NativeSevenZipArchive(new File(args[0]));
        Entry<?> entry = sevenZip.getEntry(args[1]);
        InputStream is = sevenZip.getInputStream(entry);
System.err.println("is: " + is);
//System.err.println("sevenZip: " + sevenZip.getVersion());
//System.err.println("handle: " + sevenZip.instance);
//System.err.println("size: " + sevenZip.getSelectedSize());
//System.err.println("csize: " + sevenZip.getSelectedCompressedSize());
//System.err.println("ratio: " + sevenZip.getSelectedRatio());
        System.exit(0);
    }
}

/* */
