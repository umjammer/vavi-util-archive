/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import java.io.IOException;
import java.io.InputStream;


/**
 * アーカイブを表すインターフェースです．
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public interface Archive {

    /**
     * ファイルを閉じます。
     */
    void close() throws IOException;

    /**
     * ファイルエントリの列挙を返します。
     */
    Entry[] entries();

    /**
     * 指定された名前の ZIP ファイルエントリを返します。
     */
    Entry getEntry(String name);

    /**
     * 指定された ファイルエントリの内容を読み込むための入力ストリームを
     * 返します。
     */
    InputStream getInputStream(Entry entry) throws IOException;

    /**
     * ファイルのパス名を返します。
     */
    String getName();

    /**
     * ファイル中のエントリの数を返します。
     */
    int size();
}

/* */
