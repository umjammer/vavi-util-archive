/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.apache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * The service provider for archives using apache commons-compress.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 */
public class ApacheCommonsArchiveSpi implements ArchiveSpi {

    @Override
    public boolean canExtractInput(Object target) throws IOException {
        if (!isSupported(target)) {
            return false;
        }

        InputStream is = null;

        if (target instanceof File) {
            is = new BufferedInputStream(Files.newInputStream(((File) target).toPath()));
        } else if (target instanceof InputStream) {
            is = (InputStream) target;
            if (!is.markSupported()) {
                throw new IllegalArgumentException("InputStream should support #mark()");
            }
        } else {
            assert false : target.getClass().getName();
        }

        try {
            String type = ArchiveStreamFactory.detect(is);
Debug.println("detected: " + type);
        } catch (ArchiveException e) {
Debug.println("not detected: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        if (obj instanceof File) {
            return new ApacheCommonsArchive((File) obj);
        } else if (obj instanceof InputStream) {
            return new ApacheCommonsArchive((InputStream) obj);
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class, InputStream.class};
    }

    @Override
    public String[] getFileSuffixes() {
        Set<String> suffixes = new HashSet<>();
        CompressorStreamFactory.findAvailableCompressorInputStreamProviders().forEach((name, provider) -> {
Debug.println(Level.FINER, name + ": " + StringUtil.paramString(provider));
            for (String compressorName : provider.getInputStreamCompressorNames()) {
                String[] ss = props.getProperty(compressorName).split(",", -1);
Debug.println(Level.FINER, compressorName + ": " + Arrays.toString(ss));
                suffixes.addAll(Arrays.asList(ss));
            }
        });
        return suffixes.toArray(new String[0]);
    }

    /** {compressorName: suffixes} */
    private static Properties props = new Properties();

    static {
        try {
            props.load(ApacheCommonsArchiveSpi.class.getResourceAsStream("suffixes.properties"));
        } catch (NullPointerException e) {
            throw new IllegalStateException("vavi/util/archive/apache/suffixes.properties doesn't set properly");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

/* */
