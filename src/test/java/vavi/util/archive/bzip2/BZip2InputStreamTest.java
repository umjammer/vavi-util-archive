/*
 * Copyright (c) 2009 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.bzip2;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import vavi.util.archive.spi.InputStreamSpi;


/**
 * BZip2InputStreamTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/06/12 nsano initial version <br>
 */
public class BZip2InputStreamTest {

    @Test
    public void test01() throws Exception {
        InputStreamSpi spi = new BZip2InputStreamSpi();
        spi.canExpandInput(new BufferedInputStream(Files.newInputStream(Paths.get("src/test/resources/test.tar.bz2"))));
        InputStream is = spi.createInputStreamInstance();
        byte[] b = new byte[8192];
        while (true) {
            int r = is.read(b, 0, b.length);
            if (r < 0) {
                break;
            }
        }
    }
}

/* */
