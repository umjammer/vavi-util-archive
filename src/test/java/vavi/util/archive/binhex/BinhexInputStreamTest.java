/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.binhex;

import java.io.FileInputStream;

import org.gjt.convert.binhex.BinHex4InputStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * BinhexInputStreamTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/11 umjammer initial version <br>
 */
class BinhexInputStreamTest {

    @Test
    void test() throws Exception {
        BinHex4InputStream bis = new BinHex4InputStream(new FileInputStream("src/test/resources/test.sit.hqx"));
        BinHex4InputStream.Header header = bis.getHeader();
System.err.println(header.getDataLength());
        bis.close();
        assertEquals(137270, header.getDataLength());
    }
}

/* */
