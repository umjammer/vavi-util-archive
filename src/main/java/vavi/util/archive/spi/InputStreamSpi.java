/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.spi;

import java.io.IOException;
import java.io.InputStream;


/**
 * Service Provider Interface class for {@link InputStream} of archived files.
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

    /**
     * Creates a decompressing {@link InputStream} instance.
     * TODO should have input argument
     */
    InputStream createInputStreamInstance() throws IOException;
}
