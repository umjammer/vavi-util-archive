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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
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
Debug.println(Level.FINE, "inputStreamSpi: " + StringUtil.getClassName(inputStreamSpi.getClass()));
            if (inputStreamSpi.canExpandInput(bis)) {
                InputStream inputStream = inputStreamSpi.createInputStreamInstance();
Debug.println(Level.FINE, "inputStream: " + inputStream.getClass());
                return inputStream;
            }
        }

Debug.println("no suitable spi found, use default stream");
        return bis;
    }

    /**
     * get an archive
     * TODO path is treated as a file
     */
    public static Archive getArchive(Path path) throws IOException {
        return getArchive(path.toFile());
    }

    /**
     * get an archive
     * TODO else file
     * TODO option, e.g ZipArchive's failsafe encoding
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
Debug.println(archiveSpi.getClass().getSimpleName() + ": " + e);
                canExtract = archiveSpi.canExtractInput(is);
            }
            if (canExtract) {
                Archive archive;
                try {
                    archive = archiveSpi.createArchiveInstance(file);
                } catch (IllegalArgumentException e) {
Debug.println(archiveSpi.getClass().getSimpleName() + ": " + e);
                    archive = archiveSpi.createArchiveInstance(is);
                }
Debug.println(Level.FINE, "archive: " + archive.getClass());
                return archive;
            }
        }

        is.close();
        throw new IllegalArgumentException(file + " is not supported type");
    }

    /**
     * get an archive
     * TODO option, e.g ZipArchive's failsafe encoding
     * @throws IllegalArgumentException file is not supported
     */
    public static Archive getArchive(InputStream is) throws IOException {
        for (ArchiveSpi archiveSpi : archiveSpis) {
Debug.println(Level.FINE, "archiveSpi: " + archiveSpi);
            boolean canExtract = false;

            try {
                canExtract = archiveSpi.canExtractInput(is);
            } catch (IllegalArgumentException e) {
Debug.println(archiveSpi.getClass().getSimpleName() + "::canExtractInput: " + e);
            }
            if (canExtract) {
                Archive archive;
                try {
                    archive = archiveSpi.createArchiveInstance(is);
                } catch (IllegalArgumentException e) {
Debug.println(archiveSpi.getClass().getSimpleName() + "::createArchiveInstance: " + e);
                    archive = archiveSpi.createArchiveInstance(is);
                }
Debug.println(Level.FINE, "archive: " + archive.getClass());
                return archive;
            }
        }

        is.close();
        throw new IllegalArgumentException(is.getClass().getName() + " is not supported type");
    }

    /** all suffixes */
    public static String[] getReaderFileSuffixes() {
        Set<String> suffixes = new HashSet<>();
        for (ArchiveSpi archiveSpi : archiveSpis) {
Debug.println(Level.FINE, archiveSpi.getClass().getName() + ", " + Arrays.toString(archiveSpi.getFileSuffixes()));
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
if (Debug.isLoggable(Level.FINE)) {
 archiveSpis.forEach(System.err::println);
}

            inputStreamSpis = ServiceLoader.load(vavi.util.archive.spi.InputStreamSpi.class);
if (Debug.isLoggable(Level.FINE)) {
 inputStreamSpis.forEach(System.err::println);
}
        } catch (Exception e) {
Debug.printStackTrace(e);
            System.exit(1);
        }
    }
}

/* */
