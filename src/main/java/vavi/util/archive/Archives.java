/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ServiceLoader;
import java.util.logging.Level;

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.spi.ArchiveSpi;
import vavi.util.archive.spi.InputStreamSpi;


/**
 * Archives.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/24 umjammer initial version <br>
 */
public class Archives {

    private Archives() {
    }

    /** */
    public static InputStream getInputStream(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));

        for (InputStreamSpi inputStreamSpi : inputStreamSpis) {
            if (inputStreamSpi.canExpandInput(is)) {
Debug.println(Level.FINE, "inputStreamSpi: " + StringUtil.getClassName(inputStreamSpi.getClass()));
                InputStream inputStream = inputStreamSpi.createInputStreamInstance();
Debug.println(Level.FINE, "inputStream: " + inputStream.getClass());
                return inputStream;
            }
        }

        return new FileInputStream(file);
Debug.println("no suitable spi found, use default stream");
    }

    /**
     * TODO else file
     * @throws IllegalArgumentException file is not supported
     */
    public static Archive getArchive(File file) throws IOException {
        InputStream is = new BufferedInputStream(getInputStream(file));
        for (ArchiveSpi archiveSpi : archiveSpis) {
Debug.println(Level.FINE, "archiveSpi: " + archiveSpi);
            boolean canExtract;

            try {
                canExtract = archiveSpi.canExtractInput(file);
            } catch (IllegalArgumentException e) {
Debug.println(e);
                canExtract = archiveSpi.canExtractInput(is);
            }
            if (canExtract) {
                Archive archive;
                try {
                    archive = archiveSpi.createArchiveInstance(file);
                } catch (IllegalArgumentException e) {
Debug.println(e);
                    archive = archiveSpi.createArchiveInstance(is);
                }
Debug.println(Level.FINE, "archive: " + archive.getClass());
                return archive;
            }
        }

        is.close();
        throw new IllegalArgumentException(file + " is not supported type");
    }

    private static ServiceLoader<ArchiveSpi> archiveSpis;

    private static ServiceLoader<InputStreamSpi> inputStreamSpis;

    /* collect spis */
    static {
        try {
            archiveSpis = ServiceLoader.load(vavi.util.archive.spi.ArchiveSpi.class);
archiveSpis.forEach(System.err::println);

            inputStreamSpis = ServiceLoader.load(vavi.util.archive.spi.InputStreamSpi.class);
inputStreamSpis.forEach(System.err::println);
        } catch (Exception e) {
Debug.printStackTrace(e);
            System.exit(1);
        }
    }
}

/* */
