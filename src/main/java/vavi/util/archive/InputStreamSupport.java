/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

import vavi.util.Debug;


/**
 * InputStreamSupport.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-29 nsano initial version <br>
 */
public abstract class InputStreamSupport {

    /** */
    protected InputStreamSupport() {}

    /** */
    protected File archiveFileForInputStream;

    /** */
    protected InputStreamSupport(InputStream is) throws IOException {
        Path temp = Files.createTempFile("vavi.util.archive.InputStreamSupport@" + is.hashCode(), ".inputStream");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
Debug.println(Level.FINE, "shutdownHook: rm: " + temp);
            try {
                Files.delete(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
        archiveFileForInputStream = temp.toFile();
    }
}
