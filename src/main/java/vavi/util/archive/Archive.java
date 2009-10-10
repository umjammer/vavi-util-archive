/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;

import java.io.IOException;
import java.io.InputStream;


/**
 * �A�[�J�C�u��\���C���^�[�t�F�[�X�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 021222 nsano add canExtractInput <br>
 *          0.02 021222 nsano separate canExtractInput to spi <br>
 */
public interface Archive {

    /**
     * �t�@�C������܂��B
     */
    void close() throws IOException;

    /**
     * �t�@�C���G���g���̗񋓂�Ԃ��܂��B
     */
    Entry[] entries();

    /**
     * �w�肳�ꂽ���O�� ZIP �t�@�C���G���g����Ԃ��܂��B
     */
    Entry getEntry(String name);

    /**
     * �w�肳�ꂽ �t�@�C���G���g���̓��e��ǂݍ��ނ��߂̓��̓X�g���[����
     * �Ԃ��܂��B
     */
    InputStream getInputStream(Entry entry) throws IOException;

    /**
     * �t�@�C���̃p�X����Ԃ��܂��B
     */
    String getName();

    /**
     * �t�@�C�����̃G���g���̐���Ԃ��܂��B
     */
    int size();
}

/* */
