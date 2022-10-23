/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.gca;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import vavi.util.archive.Archive;


/**
 * GCA アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 030128 nsano initial version <br>
 *          0.01 030128 nsano implements <br>
 */
public class ShellGcaArchiveSpi extends GcaArchiveSpi {

    /* */
    public Archive createArchiveInstance(Object obj, Map<String, ?> env) throws IOException {
        return new ShellGcaArchive((File) obj);
    }
}

/* */
