/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive;


/**
 * �A�[�J�C�u���ꂽ 1 �G���g����\���N���X�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 */
public interface Entry extends Cloneable {

    /**
     * �G���g���̃R�����g�������Ԃ��܂��B
     */
    String getComment();

    /**
     * ���k���ꂽ�G���g���f�[�^�̃T�C�Y��Ԃ��܂��B
     */
    long getCompressedSize();

    /**
     * ���k�������ꂽ�G���g���f�[�^�� CRC-32 �`�F�b�N�T����Ԃ��܂��B
     */
    long getCrc();

    /**
     * �G���g���̕⑫�t�B�[���h�f�[�^��Ԃ��܂��B
     */
    Object getExtra();

    /**
     * �G���g���̈��k���\�b�h��Ԃ��܂��B
     */
    int getMethod();

    /**
     * �G���g���̖��O��Ԃ��܂��B
     */
    String getName();

    /**
     * �G���g���f�[�^�̈��k�������̃T�C�Y��Ԃ��܂��B
     */
    long getSize();

    /**
     * �G���g���̏C�����Ԃ�Ԃ��܂��B
     */
    long getTime();

    /**
     * ���ꂪ�f�B���N�g���G���g���ł���ꍇ�ɁAtrue ��Ԃ��܂��B
     */
    boolean isDirectory();

    /**
     * �G���g���ɔC�ӎw��̃R�����g�������ݒ肵�܂��B
     */
    void setComment(String comment);

    /**
     * ���k���ꂽ�G���g���f�[�^�̃T�C�Y��ݒ肵�܂��B
     */
    void setCompressedSize(long csize);

    /**
     * ���k�������ꂽ�G���g���f�[�^�� CRC-32 �`�F�b�N�T����ݒ肵�܂��B
     */
    void setCrc(long crc);

    /**
     * �G���g���ɔC�ӎw��̕⑫�t�B�[���h�f�[�^��ݒ肵�܂��B
     */
    void setExtra(Object extra);

    /**
     * �G���g���̈��k���\�b�h��ݒ肵�܂��B
     */
    void setMethod(int method);

    /**
     * �G���g���f�[�^�̈��k�������̃T�C�Y��ݒ肵�܂��B
     */
    void setSize(long size);

    /**
     * �G���g���̏C�����Ԃ�ݒ肵�܂��B
     */
    void setTime(long time);

    /** TODO */
    Object getWrappedObject();
}

/* */
