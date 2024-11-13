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
import java.lang.System.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import vavi.util.archive.spi.ArchiveSpi;
import vavi.util.archive.spi.InputStreamSpi;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.TRACE;
import static java.lang.System.getLogger;


/**
 * Archives.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/24 umjammer initial version <br>
 */
public class Archives {

    private static final Logger logger = getLogger(Archives.class.getName());

    private Archives() {
    }

    /** get an archiving stream */
    public static InputStream getInputStream(File file) throws IOException {
        return getInputStream(Files.newInputStream(file.toPath()));
    }

    /** get an archiving stream */
    public static InputStream getInputStream(Path path) throws IOException {
        return getInputStream(Files.newInputStream(path));
    }

    /** get an archiving stream */
    public static InputStream getInputStream(InputStream is) throws IOException {
        InputStream bis = new BufferedInputStream(is);

        for (InputStreamSpi inputStreamSpi : inputStreamSpis) {
logger.log(TRACE, "inputStreamSpi: " + inputStreamSpi.getClass().getSimpleName());
            if (inputStreamSpi.canExpandInput(bis)) {
                InputStream inputStream = inputStreamSpi.createInputStreamInstance();
logger.log(DEBUG, "inputStream: " + inputStream.getClass());
                return inputStream;
            }
        }

logger.log(DEBUG, "no suitable spi found, use default stream");
        return bis;
    }

    /**
     * get an archive
     * @throws IllegalArgumentException file is not supported
     */
    public static Archive getArchive(Object target) throws IOException {
        return getArchive(target, Collections.emptyMap());
    }

    /**
     * get an archive
     * @throws IllegalArgumentException file is not supported
     */
    public static Archive getArchive(Object target, Map<String, ?> env) throws IOException {
        for (ArchiveSpi archiveSpi : archiveSpis) {
logger.log(TRACE, "archiveSpi: " + archiveSpi);
            boolean canExtract;

            try {
                canExtract = archiveSpi.canExtractInput(target);
            } catch (IllegalArgumentException e) {
logger.log(DEBUG, archiveSpi.getClass().getSimpleName() + ": " + e);
                continue;
            }
            if (canExtract) {
                Archive archive;
                try {
                    archive = archiveSpi.createArchiveInstance(target, env);
                } catch (IllegalArgumentException e) {
logger.log(DEBUG, archiveSpi.getClass().getSimpleName() + ": " + e);
                    continue;
                }
logger.log(DEBUG, "archive: " + archive.getClass());
                return archive;
            }
        }

        throw new IllegalArgumentException("no suitable spi for " + target.getClass().getName());
    }

    /** all suffixes */
    public static String[] getReaderFileSuffixes() {
        Set<String> suffixes = new HashSet<>();
        for (ArchiveSpi archiveSpi : archiveSpis) {
logger.log(TRACE, archiveSpi.getClass().getName() + ", " + Arrays.toString(archiveSpi.getFileSuffixes()));
            suffixes.addAll(Arrays.asList(archiveSpi.getFileSuffixes()));
        }
        return suffixes.toArray(new String[0]);
    }

    /** archive spis */
    private static ServiceLoader<ArchiveSpi> archiveSpis;

    /** archiving input stream spis */
    private static ServiceLoader<InputStreamSpi> inputStreamSpis;

    /* collect spis */
    static {
        try {
            archiveSpis = ServiceLoader.load(vavi.util.archive.spi.ArchiveSpi.class);
if (logger.isLoggable(TRACE)) {
 archiveSpis.forEach(System.err::println);
}

            inputStreamSpis = ServiceLoader.load(vavi.util.archive.spi.InputStreamSpi.class);
if (logger.isLoggable(TRACE)) {
 inputStreamSpis.forEach(System.err::println);
}
        } catch (Exception e) {
logger.log(ERROR, e.getMessage(), e);
            System.exit(1);
        }
    }
}
