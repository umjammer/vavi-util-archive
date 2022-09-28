/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.rar;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * JunrarRarArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/09/22 umjammer initial version <br>
 */
public class JunrarRarArchiveTest {

    static {
        System.setProperty("vavi.util.logging.VaviFormatter.extraClassMethod", "org\\.slf4j\\.impl\\.JDK14LoggerAdapter#(log|warn)");
    }

    @Test
    public void test() throws Exception {
        Archive archive = new JunrarRarArchive(new File("src/test/resources/test.rar"));
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName() + "\t" + LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getTime()), ZoneId.systemDefault()));
            c++;
        }
        assertEquals(6, c);
    }

    @Test
    @DisplayName("junrar doesn't support rar5")
    public void test2() throws Exception {
        Exception e = assertThrows(IOException.class, () -> {
            new JunrarRarArchive(new File("src/test/resources/rar5.rar"));
        });
        assertInstanceOf(com.github.junrar.exception.UnsupportedRarV5Exception.class, e.getCause());
    }
}

/* */
