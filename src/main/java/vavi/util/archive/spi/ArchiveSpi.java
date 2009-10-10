/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.spi;

import java.io.IOException;

import vavi.util.archive.Archive;


/**
 * Archive ÇÃ Service Provider Interface Ç≈Ç∑ÅD
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021222 nsano initial version <br>
 *          0.01 030310 nsano repackage <br>
 *          0.02 040107 nsano change interface <br>
 */
public interface ArchiveSpi {

    /**
     * âìÄÇ≈Ç´ÇÈÇ©Ç«Ç§Ç©í≤Ç◊Ç‹Ç∑ÅD
     */
    boolean canExtractInput(Object target) throws IOException;

    /** */
    Archive createArchiveInstance() throws IOException;
}

/* */
