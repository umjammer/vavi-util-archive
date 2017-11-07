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
public class AntZipArchive implements Archive {

    /** */
    private org.apache.tools.zip.ZipFile archive;

    private String name;

    /** */
    public AntZipArchive(File file) throws IOException {
        this.archive = new org.apache.tools.zip.ZipFile(file);
        this.name = file.getPath();
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
        Entry[] entries = new Entry[size()];
        Enumeration<?> e = archive.getEntries();
        for (int i = 0; e.hasMoreElements(); i++) {
            entries[i] = new AntZipEntry((org.apache.tools.zip.ZipEntry) e.nextElement());
        }
        return entries;
    }

    /**
     * 指定された名前の ZIP ファイルエントリを返します。
     * 見つからない場合は null を返します。
     */
    public Entry getEntry(String name) {
        return new AntZipEntry(archive.getEntry(name));
    }

    /**
     * 指定された ファイルエントリの内容を読み込むための入力ストリームを
     * 返します。
     */
    public InputStream getInputStream(Entry entry) throws IOException {
        return archive.getInputStream(
            (org.apache.tools.zip.ZipEntry) entry.getWrappedObject());
    }

    /**
     * ファイルのパス名を返します。
     */
    public String getName() {
        return name;
    }

    /**
     * ファイル中のエントリの数を返します。
     */
    public int size() {
        int count = 0;
        Enumeration<?> e = archive.getEntries();
        for (int i = 0; e.hasMoreElements(); i++) {
            e.nextElement();
            count++;
        }
        return count;
    }
}

/* */
