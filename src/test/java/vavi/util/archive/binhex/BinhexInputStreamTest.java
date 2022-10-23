/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.binhex;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.gjt.convert.binhex.BinHex4InputStream;
import org.junit.jupiter.api.Test;
import vavi.util.archive.Archives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


/**
 * BinhexInputStreamTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/11 umjammer initial version <br>
 */
class BinhexInputStreamTest {

    @Test
    void test() throws Exception {
        BinHex4InputStream bis = new BinHex4InputStream(Files.newInputStream(Paths.get("src/test/resources/test.sit.hqx")));
        BinHex4InputStream.Header header = bis.getHeader();
System.err.println(header.getDataLength());
        bis.close();
        assertEquals(137270, header.getDataLength());
    }

    @Test
    void test2() throws Exception {
        InputStream is = Archives.getInputStream(new File("src/test/resources/test.sit.hqx"));
        assertInstanceOf(BinHex4InputStream.class, is);
    }
}

/* */
