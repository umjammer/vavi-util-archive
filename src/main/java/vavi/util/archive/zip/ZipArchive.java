/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipFile;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * ZIP アーカイブを処理するサービスプロバイダです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class ZipArchive implements Archive {

    /** */
    private ZipFile archive;

    /** */
    public ZipArchive(File file) throws IOException {
        this.archive = new ZipFile(file);
    }

    /**
     * ファイルを閉じます。
     */
    public void close() throws IOException {
        archive.close();
    }

    /**
     * ファイルエントリの列挙を返します。
     */
    public Entry[] entries() {
        Entry[] entries = new Entry[archive.size()];
        Enumeration<?> e = archive.entries();
        for (int i = 0; e.hasMoreElements(); i++) {
            entries[i] = new ZipEntry((java.util.zip.ZipEntry) e.nextElement());
        }
        return entries;
    }

    /**
     * 指定された名前の ZIP ファイルエントリを返します。
     * 見つからない場合は null を返します。
     */
    public Entry getEntry(String name) {
        return new ZipEntry(archive.getEntry(name));
    }

    /**
     * 指定された ファイルエントリの内容を読み込むための入力ストリームを
     * 返します。
     */
    public InputStream getInputStream(Entry entry) throws IOException {
        return archive.getInputStream(
            (java.util.zip.ZipEntry) entry.getWrappedObject());
    }

    /**
     * ファイルのパス名を返します。
     */
    public String getName() {
        return archive.getName();
    }

    /**
     * ファイル中のエントリの数を返します。
     */
    public int size() {
        return archive.size();
    }
}

/* */
