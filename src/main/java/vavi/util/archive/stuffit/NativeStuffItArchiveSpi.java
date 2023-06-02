/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.stuffit;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import vavi.util.archive.Archive;


/**
 * The service provider for StuffIt archive.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060106 nsano initial version <br>
 */
public class NativeStuffItArchiveSpi extends StuffItArchiveSpi {

    @Override
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        return new NativeStuffItArchive((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }
}

/* */
