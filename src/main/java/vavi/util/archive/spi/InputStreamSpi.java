/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.spi;

import java.io.IOException;
import java.io.InputStream;


/**
 * Service Provider Interface for archived InpuStream class.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040105 nsano initial version <br>
 *          0.01 040107 nsano change interface <br>
 */
public interface InputStreamSpi {

    /**
     * Checks if extractable or not.
     */
    boolean canExpandInput(Object target) throws IOException;

    /** TODO should have input argument */
    InputStream createInputStreamInstance() throws IOException;
}

/* */
