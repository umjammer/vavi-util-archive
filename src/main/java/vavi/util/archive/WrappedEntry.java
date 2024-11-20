/*
 * Copyright (c) 2021 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;


/**
 * WrappedEntry.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/16 umjammer initial version <br>
 */
public interface WrappedEntry<T> extends Entry {

    /**
     * Returns real object for the entry.
     * TODO
     */
    T getWrappedObject();
}
