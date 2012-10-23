/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;

import org.junit.Test;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * NativeRarArchiveTest. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/02/16 umjammer initial version <br>
 */
public class NativeRarArchiveTest {

    @Test
    public void test() throws Exception {
        Archive archive = new PureJavaRarArchive(new File("/Users/nsano/Documents/Books/Comics/(一般コミック) [岩明均] ヒストリエ 第07巻.rar"));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
    }
}

/* */
