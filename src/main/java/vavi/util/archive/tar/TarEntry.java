/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.tar;

import java.util.Date;

import vavi.util.archive.Entry;


/**
 * TAR ���k�̃T�[�r�X�v���o�C�_�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040106 nsano initial version <br>
 */
public class TarEntry implements Entry {

    /** */
    private org.apache.commons.compress.archivers.tar.TarArchiveEntry entry;

    /** */
    public TarEntry(org.apache.commons.compress.archivers.tar.TarArchiveEntry entry) {
        this.entry = entry;
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
        return entry.getSize();
    }

    /**
     * ���k�������ꂽ�G���g���f�[�^�� CRC-32 �`�F�b�N�T����Ԃ��܂��B
     */
    public long getCrc() {
        return 0;
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
        return entry.getModTime().getTime();
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
        // TODO
    }

    /**
     * ���k���ꂽ�G���g���f�[�^�̃T�C�Y��ݒ肵�܂��B
     */
    public void setCompressedSize(long csize) {
        entry.setSize(csize);
    }

    /**
     * ���k�������ꂽ�G���g���f�[�^�� CRC-32 �`�F�b�N�T����ݒ肵�܂��B
     */
    public void setCrc(long crc) {
        // TODO
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
        entry.setSize(size);
    }

    /**
     * �G���g���̏C�����Ԃ�ݒ肵�܂��B
     */
    public void setTime(long time) {
        entry.setModTime(new Date(time));
    }

    /** */
    public Object getWrappedObject() {
        return entry;
    }
}

/* */
