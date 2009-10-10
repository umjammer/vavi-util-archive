/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.spi;

import vavi.util.StringUtil;
import vavi.util.archive.Entry;


/**
 * ���ʓI�ȃA�[�J�C�u�G���g���ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 030211 nsano initial version <br>
 *          0.01 030227 nsano implements clone() <br>
 */
public class CommonEntry implements Entry {

    /** */
    private String comment;
    /** */
    private long compressedSize;
    /** */
    private long crc;
    /** */
    private Object extra;
    /** */
    private int method;
    /** */
    private String name;
    /** */
    private long size;
    /** */
    private long time;
    /** */
    private boolean directory;

    /** */
    public CommonEntry() {
    }

    /**
     * �G���g���̃R�����g�������Ԃ��܂��B
     */
    public String getComment() {
        return comment;
    }

    /**
     * ���k���ꂽ�G���g���f�[�^�̃T�C�Y��Ԃ��܂��B
     */
    public long getCompressedSize() {
        return compressedSize;
    }

    /**
     * ���k�������ꂽ�G���g���f�[�^�� CRC-32 �`�F�b�N�T����Ԃ��܂��B
     */
    public long getCrc() {
        return crc;
    }

    /**
     * �G���g���̕⑫�t�B�[���h�f�[�^��Ԃ��܂��B
     */
    public Object getExtra() {
        return extra;
    }

    /**
     * �G���g���̈��k���\�b�h��Ԃ��܂��B
     */
    public int getMethod() {
        return method;
    }

    /**
     * �G���g���̖��O��Ԃ��܂��B
     */
    public String getName() {
        return name;
    }

    /**
     * �G���g���̖��O��ݒ肵�܂��B
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * �G���g���f�[�^�̈��k�������̃T�C�Y��Ԃ��܂��B
     */
    public long getSize() {
        return size;
    }

    /**
     * �G���g���̏C�����Ԃ�Ԃ��܂��B
     */
    public long getTime() {
        return time;
    }

    /**
     * ���ꂪ�f�B���N�g���G���g���ł���ꍇ�ɁAtrue ��Ԃ��܂��B
     */
    public boolean isDirectory() {
        return directory;
    }

    /** */
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    /**
     * �G���g���ɔC�ӎw��̃R�����g�������ݒ肵�܂��B
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * ���k���ꂽ�G���g���f�[�^�̃T�C�Y��ݒ肵�܂��B
     */
    public void setCompressedSize(long csize) {
        this.compressedSize = csize;
    }

    /**
     * ���k�������ꂽ�G���g���f�[�^�� CRC-32 �`�F�b�N�T����ݒ肵�܂��B
     */
    public void setCrc(long crc) {
        this.crc = crc;
    }

    /**
     * �G���g���ɔC�ӎw��̕⑫�t�B�[���h�f�[�^��ݒ肵�܂��B
     * @param extra Cloneable �łȂ��Ă͂����܂���
     */
    public void setExtra(Object extra) {
        this.extra = extra;
    }

    /**
     * �G���g���̈��k���\�b�h��ݒ肵�܂��B
     */
    public void setMethod(int method) {
        this.method = method;
    }

    /**
     * �G���g���f�[�^�̈��k�������̃T�C�Y��ݒ肵�܂��B
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * �G���g���̏C�����Ԃ�ݒ肵�܂��B
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * ���̃G���g���̃R�s�[��Ԃ��܂��B
     * (deep copy, else extra(shallow copy))
     */
    public Object clone() {
        try {
            CommonEntry clone = (CommonEntry) Class.forName(getClass().getName()).newInstance();
            clone.comment = (comment == null) ? null : new String(comment);
            clone.compressedSize = compressedSize;
            clone.crc = crc;
            clone.method = method;
            clone.name = (name == null) ? null : new String(name);
            clone.size = size;
            clone.time = time;
            clone.directory = directory;
            return clone;
        } catch (Exception e) {
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }
    }

    /** */
    public Object getWrappedObject() {
        return this;
    }

    /** */
    public String toString() {
        return StringUtil.paramString(this);
    }
}

/* */
