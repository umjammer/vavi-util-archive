/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.spi;

import java.io.IOException;
import java.io.InputStream;


/**
 * Archive �� Service Provider Interface �ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040105 nsano initial version <br>
 *          0.01 040107 nsano change interface <br>
 */
public interface InputStreamSpi {

    /**
     * �𓀂ł��邩�ǂ������ׂ܂��D
     */
    boolean canExpandInput(Object target) throws IOException;

    /** */
    InputStream createInputStreamInstance() throws IOException;
}

/* */
