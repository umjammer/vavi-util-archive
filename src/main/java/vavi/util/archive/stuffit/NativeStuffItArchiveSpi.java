/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.stuffit;

import java.io.File;
import java.io.IOException;

import vavi.util.archive.Archive;


/**
 * StuffIt アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060106 nsano initial version <br>
 */
public class NativeStuffItArchiveSpi extends StuffItArchiveSpi {

    /* */
    public Archive createArchiveInstance(Object obj) throws IOException {
        return new NativeStuffItArchive((File) obj);
    }

    @Override
    public Class<?>[] getInputTypes() {
        return new Class[] {File.class};
    }
}

/* */
