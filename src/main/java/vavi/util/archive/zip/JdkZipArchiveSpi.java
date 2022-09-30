/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;

import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.spi.ArchiveSpi;


/**
 * The ZIP SPI using the JDK zip library.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 *          0.01 030128 nsano implements <br>
 */
public class JdkZipArchiveSpi extends ZipArchiveSpi {

    @Override
    public boolean canExtractInput(Object target) throws IOException {

        if (!isSupported(target)) {
            return false;
        }

        InputStream is = null;
        boolean needToClose = false;

        if (target instanceof File) {
            is = new BufferedInputStream(Files.newInputStream(((File) target).toPath()));
            needToClose = true;
        } else if (target instanceof InputStream) {
            is = (InputStream) target;
            if (!is.markSupported()) {
                throw new IllegalArgumentException("InputStream should support #mark()");
            }
        } else {
            assert false : target.getClass().getName();
        }

        return super.canExtractInput(is, needToClose);
    }

    /** */
    public static final String ENV_KEY_FAILSAFE_ENCODING = "failsafeEncoding";

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        String failsafeEncoding = (String) env.get(ENV_KEY_FAILSAFE_ENCODING);
Debug.println(Level.FINE, "failsafeEncoding: " + failsafeEncoding);

        if (obj instanceof File) {
            return new JdkZipArchive((File) obj, failsafeEncoding);
        } else if (obj instanceof InputStream) {
            return new JdkZipArchive((InputStream) obj, failsafeEncoding);
        } else {
            throw new IllegalArgumentException("not supported type " + obj.getClass().getName());
        }
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class, InputStream.class};
    }
}

/* */
