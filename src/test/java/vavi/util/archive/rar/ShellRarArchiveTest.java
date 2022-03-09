/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;


/**
 * ShellRarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
class ShellRarArchiveTest {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void test() throws Exception {
        main(new String[] { "src/test/resources/test.rar" });
    }

    //----

    /** */
    public static void main(String[] args) throws Exception {
        new ShellRarArchive(new File(args[0]));
    }
}

/* */
