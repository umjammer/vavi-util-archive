/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.lha;

import jp.gr.java_conf.dangan.util.lha.CompressMethod;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;

import vavi.util.archive.Entry;


/**
 * LHA ���k�̃T�[�r�X�v���o�C�_�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021103 nsano initial version <br>
 *          0.01 030211 nsano implements #isDirectory() <br>
 *          0.02 030211 nsano jLha 0.06 compatible <br>
 */
public class LhaEntry implements Entry {

    /** */
    private LhaHeader entry;

    /** */
    public LhaEntry(LhaHeader header) {
        this.entry = header;
    }

    /**
     * �G���g���̃R�����g�������Ԃ��܂��B
     */
    public String getComment() {
        return null;
    }

    /**
     * ���k���ꂽ�G���g���f�[�^�̃T�C�Y��Ԃ��܂��B
     */
    public long getCompressedSize() {
        return entry.getCompressedSize();
    }

    /**
     * ���k�������ꂽ�G���g���f�[�^�� CRC-32 �`�F�b�N�T����Ԃ��܂��B
     */
    public long getCrc() {
        return entry.getCRC();
    }

    /**
     * �G���g���̕⑫�t�B�[���h�f�[�^��Ԃ��܂��B
     */
    public Object getExtra() {
        return null;
    }

    /**
     * �G���g���̈��k���\�b�h��Ԃ��܂��B
     */
    public int getMethod() {
        return 0;
    }

    /**
     * �G���g���̖��O��Ԃ��܂��B
     */
    public String getName() {
        return entry.getPath();
    }

    /**
     * �G���g���f�[�^�̈��k�������̃T�C�Y��Ԃ��܂��B
     */
    public long getSize() {
        return entry.getOriginalSize();
    }

    /**
     * �G���g���̏C�����Ԃ�Ԃ��܂��B
     */
    public long getTime() {
        return entry.getLastModified().getTime();
    }

    /**
     * ���ꂪ�f�B���N�g���G���g���ł���ꍇ�ɁAtrue ��Ԃ��܂��B
     */
    public boolean isDirectory() {
        return CompressMethod.LHD.equals(entry.getCompressMethod());
    }

    /**
     * �G���g���ɔC�ӎw��̃R�����g�������ݒ肵�܂��B
     */
    public void setComment(String comment) {
        // TODO
    }

    /**
     * ���k���ꂽ�G���g���f�[�^�̃T�C�Y��ݒ肵�܂��B
     */
    public void setCompressedSize(long csize) {
        entry.setCompressedSize(csize);
    }

    /**
     * ���k�������ꂽ�G���g���f�[�^�� CRC-32 �`�F�b�N�T����ݒ肵�܂��B
     */
    public void setCrc(long crc) {
        entry.setCRC((int) crc);
    }

    /**
     * �G���g���ɔC�ӎw��̕⑫�t�B�[���h�f�[�^��ݒ肵�܂��B
     */
    public void setExtra(Object extra) {
        // TODO
    }

    /**
     * �G���g���̈��k���\�b�h��ݒ肵�܂��B
     */
    public void setMethod(int method) {
        // TODO
    }

    /**
     * �G���g���f�[�^�̈��k�������̃T�C�Y��ݒ肵�܂��B
     */
    public void setSize(long size) {
        entry.setOriginalSize(size);
    }

    /**
     * �G���g���̏C�����Ԃ�ݒ肵�܂��B
     */
    public void setTime(long time) {
        // TODO
    }

    /**
     * ���̃G���g���̃R�s�[��Ԃ��܂��B
     */
    public Object clone() {
        return entry.clone();
    }

    /** */
    public Object getWrappedObject() {
        return entry;
    }
}

/* */
