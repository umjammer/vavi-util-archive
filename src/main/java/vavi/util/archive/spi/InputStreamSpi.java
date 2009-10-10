/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.spi;

import java.io.IOException;
import java.io.InputStream;


/**
 * Archive ÇÃ Service Provider Interface Ç≈Ç∑ÅD
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040105 nsano initial version <br>
 *          0.01 040107 nsano change interface <br>
 */
public interface InputStreamSpi {

    /**
     * âìÄÇ≈Ç´ÇÈÇ©Ç«Ç§Ç©í≤Ç◊Ç‹Ç∑ÅD
     */
    boolean canExpandInput(Object target) throws IOException;

    /** */
    InputStream createInputStreamInstance() throws IOException;
}

/* */
