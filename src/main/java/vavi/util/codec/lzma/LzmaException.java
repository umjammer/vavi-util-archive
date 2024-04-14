/*
 * Copyright (c) 1999-2004 Igor Pavlov
 *
 * Modified by Naohide Sano
 */

package vavi.util.codec.lzma;

import java.io.IOException;


/**
 * LzmaException.
 *
 * @author Igor Pavlov
 * @author myspace
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 040916 nsano modified <br>
 */
public class LzmaException extends IOException {

    /** */
    public LzmaException() {
    }

    /** */
    public LzmaException(String message) {
        super(message);
    }
}
