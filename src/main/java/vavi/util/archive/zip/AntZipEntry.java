/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import vavi.util.archive.Entry;


/**
 * ZIP ���k�̃T�[�r�X�v���o�C�_�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 021104 nsano initial version <br>
 */
public class AntZipEntry implements Entry {

    /** */
    private org.apache.tools.zip.ZipEntry entry;

    /** */
    public AntZipEntry(org.apache.tools.zip.ZipEntry entry) {
        this.entry = entry;
    }

    /**
     * �G���g���̃R�����g�������Ԃ��܂��B
     */
    public String getComment() {
        return entry.getComment();
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
        return entry.getCrc();
    }

    /**
     * �G���g���̕⑫�t�B�[���h�f�[�^��Ԃ��܂��B
     */
    public Object getExtra() {
        return entry.getExtra();
    }

    /**
     * �G���g���̈��k���\�b�h��Ԃ��܂��B
     */
    public int getMethod() {
        return entry.getMethod();
    }

    /**
     * �G���g���̖��O��Ԃ��܂��B
     */
    public String getName() {
        return entry.getName();
    }

    /**
     * �G���g���f�[�^�̈��k�������̃T�C�Y��Ԃ��܂��B
     */
    public long getSize() {
        return entry.getSize();
    }

    /**
     * �G���g���̏C�����Ԃ�Ԃ��܂��B
     */
    public long getTime() {
        return entry.getTime();
    }

    /**
     * ���ꂪ�f�B���N�g���G���g���ł���ꍇ�ɁAtrue ��Ԃ��܂��B
     */
    public boolean isDirectory() {
        return entry.isDirectory();
    }

    /**
     * �G���g���ɔC�ӎw��̃R�����g�������ݒ肵�܂��B
     */
    public void setComment(String comment) {
        entry.setComment(comment);
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
        entry.setCrc(crc);
    }

    /**
     * �G���g���ɔC�ӎw��̕⑫�t�B�[���h�f�[�^��ݒ肵�܂��B
     */
    public void setExtra(Object extra) {
        entry.setExtra((byte[]) extra);
    }

    /**
     * �G���g���̈��k���\�b�h��ݒ肵�܂��B
     */
    public void setMethod(int method) {
        entry.setMethod(method);
    }

    /**
     * �G���g���f�[�^�̈��k�������̃T�C�Y��ݒ肵�܂��B
     */
    public void setSize(long size) {
        entry.setSize(size);
    }

    /**
     * �G���g���̏C�����Ԃ�ݒ肵�܂��B
     */
    public void setTime(long time) {
        entry.setTime(time);
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
