/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.spi;

import java.io.IOException;

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
     * 解凍できるかどうか調べます．
     */
    boolean canExtractInput(Object target) throws IOException;

    /** */
    Archive createArchiveInstance(Object obj) throws IOException;
}

/* */
