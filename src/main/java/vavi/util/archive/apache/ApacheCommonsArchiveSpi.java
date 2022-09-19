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
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.compress.compressors.CompressorStreamFactory;
import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * The SPI for archives using Apache commons compress.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 */
public class ApacheCommonsArchiveSpi implements ArchiveSpi {

    /**
     * @param target currently accept {@link File} only.
     */
    @Override
    public boolean canExtractInput(Object target) throws IOException {

        if (!(target instanceof File)) {
            throw new IllegalArgumentException("not supported type " + target);
        }

        InputStream is =
            new BufferedInputStream(Files.newInputStream(((File) target).toPath()));

        // TODO accepts all
        return true;
    }

    @Override
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new ApacheCommonsArchive((File) obj);
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
