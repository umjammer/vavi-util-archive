/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.gca;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import vavi.util.archive.Archive;

import static java.lang.System.getLogger;


/**
 * The service provider for GCA archive, read by gcac.exe running on a jdosbox machine.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 260822 nsano initial version <br>
 * @see DosboxGcaArchive
 */
public class DosboxGcaArchiveSpi extends GcaArchiveSpi {

    private static final Logger logger = getLogger(DosboxGcaArchiveSpi.class.getName());

    @Override
    public boolean canExtractInput(Object target) throws IOException {
        if (!super.canExtractInput(target)) {
            return false;
        }
        // this provider is a gcac.exe and a machine to run it on; without either of them
        // the file is a gca all the same, just not one for this provider to open
        return isAvailable();
    }

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        return new DosboxGcaArchive((File) obj);
    }

    /** are the gcac.exe to run and the emulator to run it on both here? */
    static boolean isAvailable() {
        Path gcac = Path.of(System.getProperty(DosboxGcaArchive.GCAC, DosboxGcaArchive.DEFAULT_GCAC));
        if (!Files.exists(gcac)) {
logger.log(Level.DEBUG, "no gcac.exe to run: " + gcac);
            return false;
        }
        try {
            Class.forName("jdos.api.JDosBox");
        } catch (ClassNotFoundException e) {
logger.log(Level.DEBUG, "no jdosbox to run gcac.exe on");
            return false;
        }
        return true;
    }
}
