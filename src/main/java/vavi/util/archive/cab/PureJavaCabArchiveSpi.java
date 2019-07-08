/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import java.io.File;
import java.io.IOException;

import vavi.util.archive.Archive;


/**
 * CAB アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 041002 nsano initial version <br>
 */
public class PureJavaCabArchiveSpi extends CabArchiveSpi {

    /* */
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new PureJavaCabArchive((File) obj);
    }
}

/* */
