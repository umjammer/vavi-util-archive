/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import jp.gr.java_conf.dangan.util.lha.LhaInputStream;


/**
 * LHA アーカイブを処理するサービスプロバイダです．
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public class LhaArchive implements Archive {

    /** */
    private Object archive;

    /** */
    public LhaArchive(File file) throws IOException {
        this.archive = new LhaFile(file);
    }

    /** */
    public LhaArchive(InputStream is) throws IOException {
        this.archive = new LhaInputStream(is);
    }

    /**
     * ファイルを閉じます。
     */
    public void close() throws IOException {
        if (LhaFile.class.isInstance(archive)) {
            LhaFile.class.cast(archive).close();
        } else if (LhaInputStream.class.isInstance(archive)) {
            LhaInputStream.class.cast(archive).close();
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    /**
     * ファイルエントリの列挙を返します。
     */
    public Entry<?>[] entries() {
        if (LhaFile.class.isInstance(archive)) {
            LhaHeader[] headers = LhaFile.class.cast(archive).getEntries();
            Entry<?>[] entries = new Entry[headers.length];
            for (int i = 0; i < headers.length; i++) {
                entries[i] = new LhaEntry(headers[i]);
            }
            return entries;
        } else if (LhaInputStream.class.isInstance(archive)) {
            try {
                LhaInputStream lis = LhaInputStream.class.cast(archive);
                List<Entry<?>> entries = new ArrayList<>();
                LhaHeader header;
                while ((header = lis.getNextEntry()) != null) {
                    entries.add(new LhaEntry(header));
                }
                return entries.toArray(new Entry[entries.size()]);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    /**
     * 指定された名前のファイルエントリを返します。 見つからない場合は null を返します。
     */
    public Entry<?> getEntry(String name) {
        if (LhaFile.class.isInstance(archive)) {
            LhaHeader[] headers = LhaFile.class.cast(archive).getEntries();
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].getPath().equals(name)) {
                    return new LhaEntry(headers[i]);
                }
            }
            return null;
        } else if (LhaInputStream.class.isInstance(archive)) {
            try {
                LhaInputStream lis = LhaInputStream.class.cast(archive);
                LhaHeader header;
                while ((header = lis.getNextEntry()) != null) {
                    if (name.equals(header.getPath())) {
                        return new LhaEntry(header);
                    }
                }
                return null;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    /**
     * 指定された ファイルエントリの内容を読み込むための入力ストリームを 返します。
     */
    public InputStream getInputStream(Entry<?> entry) throws IOException {
        if (LhaFile.class.isInstance(archive)) {
            return LhaFile.class.cast(archive).getInputStream(LhaHeader.class.cast(entry.getWrappedObject()));
        } else if (LhaInputStream.class.isInstance(archive)) {
            LhaInputStream lis = LhaInputStream.class.cast(archive);
            LhaHeader header;
            while ((header = lis.getNextEntry()) != null) {
                if (entry.getName().equals(header.getPath())) {
                    return lis;
                }
            }
            return null;
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    /**
     * ファイルのパス名を返します。
     */
    public String getName() {
        if (LhaFile.class.isInstance(archive)) {
            return null; // TODO LhaFile.class.cast(archive).;
        } else if (LhaInputStream.class.isInstance(archive)) {
            return LhaInputStream.class.cast(archive).toString();
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }

    /**
     * ファイル中のエントリの数を返します。
     */
    public int size() {
        if (LhaFile.class.isInstance(archive)) {
            return LhaFile.class.cast(archive).size();
        } else if (LhaInputStream.class.isInstance(archive)) {
            return -1;
        } else {
            throw new IllegalStateException(archive.getClass().getName());
        }
    }
}

/* */
