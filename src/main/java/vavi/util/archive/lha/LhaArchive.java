/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;


/**
 * LHA アーカイブを処理するサービスプロバイダです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class LhaArchive implements Archive {

    /** */
    private LhaFile archive;

    /** */
    public LhaArchive(File file) throws IOException {
        this.archive = new LhaFile(file);
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
        LhaHeader[] headers = archive.getEntries();
        Entry[] entries = new Entry[headers.length];
        for (int i = 0; i < headers.length; i++) {
            entries[i] = new LhaEntry(headers[i]);
        }
        return entries;
    }

    /**
     * 指定された名前のファイルエントリを返します。
     * 見つからない場合は null を返します。
     */
    public Entry getEntry(String name) {
        LhaHeader[] headers = archive.getEntries();
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].getPath().equals(name)) {
                return new LhaEntry(headers[i]);
            }
        }
        return null;
    }

    /**
     * 指定された ファイルエントリの内容を読み込むための入力ストリームを
     * 返します。
     */
    public InputStream getInputStream(Entry entry) throws IOException {
        return archive.getInputStream((LhaHeader) entry.getWrappedObject());
    }

    /**
     * ファイルのパス名を返します。
     */
    public String getName() {
        return null;
    }

    /**
     * ファイル中のエントリの数を返します。
     */
    public int size() {
        return archive.size();
    }
}

/* */
