/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

#include <jni.h>
#include <windows.h>
#include <unrar32.h>
#include "vavi_util_archive_vavi_NativeRarArchive.h"


/*
 * RAR ラッパ
 *
 * @todo	_archiveHandle を使用するときにロックする必要がある???
 *
 * @author	<a href=mailto:vavivavi@yahoo.co.jp>nsano</a>
 * @version	0.00	071110	nsano	initial version <br>
 */

//-----------------------------------------------------------------------------

#define M_ERROR_MESSAGE_OFF	0x00800000L

//-----------------------------------------------------------------------------

/**
 * アーカイブのハンドルを取得します。
 */
static HARC getArchiveHandle(JNIEnv *env, jobject obj) {
    jclass class = (*env)->GetObjectClass(env, obj);
    jfieldID field = (*env)->GetFieldID(env, class, "instance", "I");
    return (HARC) (*env)->GetIntField(env, obj, field);
}

/**
 * 例外を投げます。
 * @param exception "java/lang/Exception"
 */
static void throwExceptionWithStringMessage(JNIEnv *env, char *exception, char *_message) {

    jclass class = (*env)->FindClass(env, exception);
    (*env)->ThrowNew(env, class, _message);
}

/**
 * 例外を投げます。
 * @param exception "java/lang/Exception"
 */
static void throwExceptionWithIntMessage(JNIEnv *env, char *exception, int _message) {
    char _buf[64];
    sprintf(_buf, "%d", _message);

    throwExceptionWithStringMessage(env, exception, _buf);
}

/**
 * 圧縮方法の数値を返します。
 */
static int getMethodNumber(char *method) {
fprintf(stderr, "method: %s\n", method);
fflush(stderr);
    if (!strcmp(method, "PPMD")) {
        return 0;
    } else {
        return -1;
    }
}

//-----------------------------------------------------------------------------

/**
 * コマンド文字列を与えて，各種の書庫操作を行います。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    exec
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_exec(JNIEnv *env, jobject obj, jstring command) {
    char _buf[8192];
    const char *_command = (*env)->GetStringUTFChars(env, command, NULL);
    int result = Unrar(NULL, _command, _buf, 8192);
    (*env)->ReleaseStringUTFChars(env, command, _command);

    if (result != 0) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }
}

/**
 * バージョンを返します。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getVersion(JNIEnv *env, jobject obj) {
    UnrarGetRunning();	// dummy
//fprintf(stderr, "isRunning: %d\n", UnrarGetRunning());
//fprintf(stderr, "version: %d\n", UnrarGetVersion());
    return (jint) UnrarGetVersion();
}

/**
 * 動作中か否かを得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    isRunning
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_isRunning(JNIEnv *env, jobject obj) {
    return (jboolean) UnrarGetRunning();
}

/**
 * 指定ファイルが書庫として正しいかどうかを返します。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    checkArchive
 * Signature: (Ljava/lang/String;I)Z
 */
JNIEXPORT jboolean JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_checkArchive(JNIEnv *env, jobject obj, jstring filename, jint mode) {

    const char *_filename = (*env)->GetStringUTFChars(env, filename, NULL);
    jboolean result = (jboolean) UnrarCheckArchive(_filename, mode);
    (*env)->ReleaseStringUTFChars(env, filename, _filename);

    return result;
}

/**
 * 指定された書庫ファイルに格納されているファイル数を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getFileCount
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getFileCount(JNIEnv *env, jobject obj, jstring filename) {
    const char *_filename = (*env)->GetStringUTFChars(env, filename, NULL);
    int result = (jboolean) UnrarGetFileCount(_filename);
    (*env)->ReleaseStringUTFChars(env, filename, _filename);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 書庫ファイルを開きます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    openArchive
 * Signature: (Ljava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_openArchive(JNIEnv *env, jobject obj, jstring filename, jint mode) {

    mode |= M_ERROR_MESSAGE_OFF;

    const char *_filename = (*env)->GetStringUTFChars(env, filename, NULL);
    HARC _archiveHandle = UnrarOpenArchive(NULL, _filename, mode);
    (*env)->ReleaseStringUTFChars(env, filename, _filename);

    if (_archiveHandle == (void *) NULL) {
        throwExceptionWithIntMessage(env, "java/io/IOException", -1);
    } else {
//fprintf(stderr, "handle: %d\n", _archiveHandle);
        jclass class = (*env)->GetObjectClass(env, obj);
        jfieldID field = (*env)->GetFieldID(env, class, "instance", "I");
        (*env)->SetIntField(env, obj, field, (jint) _archiveHandle);
    }
}

/**
 * 書庫ファイルを閉じます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    closeArchive
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_closeArchive(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    int result = UnrarCloseArchive(_archiveHandle);

    if (result != 0) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }
}

/**
 * 最初の格納ファイルの情報を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    findFirst
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_findFirst(JNIEnv *env, jobject obj, jstring key) {

    HARC _archiveHandle = getArchiveHandle(env, obj);
//fprintf(stderr, "handle: %d\n", _archiveHandle);

    INDIVIDUALINFO _entry;

    const char *_key = (*env)->GetStringUTFChars(env, key, NULL);
    int result = UnrarFindFirst(_archiveHandle, _key, &_entry);
    (*env)->ReleaseStringUTFChars(env, key, _key);

    if (result != 0) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    if (result == 0) {

        // getFileName がうまくいってないので代用
        jstring filename = (*env)->NewStringUTF(env, _entry.szFileName);
//fprintf(stderr, "filename: %s\n", _entry.szFileName);
        jclass class = (*env)->GetObjectClass(env, obj);
        jfieldID field = (*env)->GetFieldID(env, class,
            "currentFilename", "Ljava/lang/String;");
        (*env)->SetObjectField(env, obj, field, filename);

        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}

/**
 * 2 番目以降の格納ファイルの情報を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    findNext
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_findNext(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    INDIVIDUALINFO _entry;

    int result = (jboolean) UnrarFindNext(_archiveHandle, &_entry);

    if (result == 0) {

        // getFileName がうまくいってないので代用
        jstring filename = (*env)->NewStringUTF(env, _entry.szFileName);
//fprintf(stderr, "filename: %s\n", _entry.szFileName);
        jclass class = (*env)->GetObjectClass(env, obj);
        jfieldID field = (*env)->GetFieldID(env, class,
            "currentFilename", "Ljava/lang/String;");
        (*env)->SetObjectField(env, obj, field, filename);

        return JNI_TRUE;
    } else {
        return JNI_FALSE;
    }
}

/**
 * 格納ファイルのファイル名を得ます。
 *
 * @@@ なんかうまいこといってない
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getCurrentFileName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getCurrentFileName(JNIEnv *env, jobject obj) {
    HARC _archiveHandle = getArchiveHandle(env, obj);

    unsigned char _buf[513];

    int result = UnrarGetFileName(_archiveHandle, _buf, 512);

    if (result != 0) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
        return NULL;
    } else {
/*
fprintf(stderr, "%s\n", _buf);
int i;
for (i = 0; i < strlen(_buf); i++) {
 fprintf(stderr, "%02X ", _buf[i]);
}
fprintf(stderr, "\n");
fflush(stderr);
*/
        return (*env)->NewStringUTF(env, _buf);
    }
}

/**
 * 格納ファイルの圧縮法を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getCurrentMethod
 * Signature: ()I;
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getCurrentMethod(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    char _buf[8];

    int result = UnrarGetMethod(_archiveHandle, _buf, 8);

    if (result != 0) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
        return -1;
    } else {
        return getMethodNumber(_buf);
    }
}

/**
 * 格納ファイルのサイズを得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getCurrentOriginalSize
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getCurrentOriginalSize(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    unsigned long result = UnrarGetOriginalSize(_archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 格納ファイルの圧縮サイズを得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getCurrentCompressedSize
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getCurrentCompressedSize(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    unsigned long result = UnrarGetCompressedSize(_archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 格納ファイルの日付を DOS 形式で得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getCurrentDate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getCurrentDate(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    unsigned int result = UnrarGetDate(_archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 格納ファイルの時刻を DOS 形式で得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getCurrentTime
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getCurrentTime(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    unsigned int result = UnrarGetTime(_archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 格納ファイルのチェックサムを得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getCurrentCRC
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getCurrentCRC(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    unsigned long result = UnrarGetCRC(_archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 検索にマッチしたファイルのサイズの合計を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getSelectedSize
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getSelectedSize(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    long result = UnrarGetArcOriginalSize(_archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 検索にマッチしたファイルの圧縮サイズの合計を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getSelectedCompressedSize
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getSelectedCompressedSize(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    long result = UnrarGetArcCompressedSize(_archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 検索にマッチしたファイルの全体の圧縮率を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeRarArchive
 * Method:    getSelectedRatio
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeRarArchive_getSelectedRatio(JNIEnv *env, jobject obj) {

    HARC _archiveHandle = getArchiveHandle(env, obj);

    int result = UnrarGetArcRatio(_archiveHandle);
//fprintf(stderr, "result: %d\n", result);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/* */
