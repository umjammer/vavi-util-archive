/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.spi;

import java.io.IOException;
import java.util.Map;

import vavi.util.archive.Archive;


/**
 * Service Provider Interface for Archive class.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 *          0.01 030310 nsano repackage <br>
 *          0.02 040107 nsano change interface <br>
 */
public interface ArchiveSpi {

    /**
     * Checks a target is extractable or not.
     * @throws IllegalArgumentException next provider
     */
    boolean canExtractInput(Object target) throws IOException;

    /** */
    Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException;

    /** TODO */
    Class<?>[] getInputTypes();

    /** TODO */
    String[] getFileSuffixes();

    /** */
    default boolean isSupported(Object target) {
        for (Class<?> c : getInputTypes()) {
            if (c.isInstance(target)) {
                return true;
            }
        }
        return false;
    }

    static boolean isEnabled(String key, Map<String, Object> map) {
        return map.containsKey(key) && (map.get(key) == null || (boolean) map.get(key));
    }
}

/* */
