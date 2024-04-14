/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import java.io.IOException;
import java.io.InputStream;


/**
 * Represents archived data.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public interface Archive {

    /**
     * Closes the archived file.
     * <p>
     * Closing this archived file will close all of the input streams
     * previously returned by invocations of the getInputStream method.
     * </p>
     * @throws IOException if an I/O error has occurred
     */
    void close() throws IOException;

    /**
     * Returns an enumeration of the archived file entries.
     *
     * @return an enumeration of the archived file entries
     * @throws IllegalStateException if the archived file has been closed
     */
    Entry[] entries();

    /**
     * Returns the archived file entry for the specified name, or null if not found.
     * 
     * @param name the name of the entry TODO make spec. e.g zip is need to strip '/' at the head
     * @return the archived file entry, or null if not found
     * @throws IllegalStateException if the archived file has been closed
     */
    Entry getEntry(String name);

    /**
     * Returns an input stream for reading the contents of the specified archived file entry.
     * <p>
     * Closing this archived file will, in turn, close all input streams that have been returned
     * by invocations of this method.
     * </p>
     * @param entry the archived file entry
     * @return the input stream for reading the contents of the specified archived file entry.
     * @throws IOException if an I/O error has occurred
     * @throws IllegalStateException if the archived file has been closed
     */
    InputStream getInputStream(Entry entry) throws IOException;

    /**
     * Returns the path name of the archived file.
     *
     * @return the path name of the archived file
     */
    String getName();

    /**
     * Returns the number of entries in the archived file.
     *
     * @return the number of entries in the archived file
     * @throws IllegalStateException if the archived file has been closed
     */
    int size();
}
