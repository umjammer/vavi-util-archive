/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * ComCabArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/04/08 umjammer initial version <br>
 */
class ComCabArchiveTest {

    @Test
    void test() {
        fail("Not yet implemented");
    }

    //----

    /** */
    public static void main(String[] args) throws Exception {
        ComCabArchive rar = new ComCabArchive(new File(args[0]));
        Entry entry = rar.getEntry(args[1]);
System.err.println("entry: " + entry);
        InputStream is = rar.getInputStream(entry);
System.err.println("is: " + is);
    }
}

/* */
