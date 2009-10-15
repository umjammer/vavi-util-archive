/*
 * Copyright (c) 2003 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

typedef long long ULHA_INT64;

#include <jni.h>
#include <windows.h>
#include <UnGCA32.h>
#include "vavi_util_archive_vavi_NativeGcaArchive.h"


/*
 * Gca ラッパ
 *
 * @todo	_archiveHandle を使用するときにロックする必要がある???
 *
 * @author	<a href=mailto:vavivavi@yahoo.co.jp>nsano</a>
 * @version	0.00	030226	nsano	initial version <br>
 *		0.00	030228	nsano	complete <br>
 */

//-----------------------------------------------------------------------------

#define M_ERROR_MESSAGE_OFF	0x00800000L


//-----------------------------------------------------------------------------

/**
 * アーカイブのハンドルを取得します。
 * @return NativeGcaArchive#instance
 */
static jint getArchiveHandle(JNIEnv *env, jobject obj) {
    jclass class = (*env)->GetObjectClass(env, obj);
    jfieldID field = (*env)->GetFieldID(env, class, "instance", "I");
    return (*env)->GetIntField(env, obj, field);
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
 * @param method 0: "GCA0" -1: else
 */
static int getMethodNumber(char *method) {
    if (!strcmp(method, "GCA0")) {
        return 0;
    } else {
        return -1;
    }
}

//-----------------------------------------------------------------------------

/**
 * コマンド文字列を与えて，各種の書庫操作を行います。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    exec
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_exec(JNIEnv *env, jobject obj, jstring command) {
    char *_buf = malloc(1024);
    jboolean isCopy;
    const char *_command = (*env)->GetStringUTFChars(env, command, &isCopy);
fprintf(stderr, "native: %s, (%d)\n", _command, isCopy);
fflush(stderr);
    jclass class = (*env)->GetObjectClass(env, obj);
    jfieldID field = (*env)->GetFieldID(env, class, "windowsHandle", "I");
    HWND hWnd = (HWND) (*env)->GetIntField(env, obj, field);
fprintf(stderr, "hWnd: %08x\n", (unsigned int) hWnd);
fflush(stderr);

    int result = UnGCA(hWnd, _command, _buf, 10);
fprintf(stderr, "here\n");
fflush(stderr);
    (*env)->ReleaseStringUTFChars(env, command, _command);
//{int i, j;
//for(j = 0; j < 8; j++) {
// for(i = 0; i < 16; i++) {
//  fprintf(stderr, "%0X ", _buf[j * 16 + i]);
// }
// fprintf(stderr, "\n");
//}
//fflush(stderr);
//}
    free(_buf);

    if (result != 0) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }
}

/**
 * バージョンを返します。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getVersion(JNIEnv *env, jobject obj) {
    UnGCAGetRunning();	// dummy
//fprintf(stderr, "isRunning: %d\n", UnGCAGetRunning());
//fprintf(stderr, "version: %d\n", UnGCAGetVersion());
    return (jint) UnGCAGetVersion();
}

/**
 * 動作中か否かを得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    isRunning
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_isRunning(JNIEnv *env, jobject obj) {
    return (jboolean) UnGCAGetRunning();
}

/**
 * 指定ファイルが書庫として正しいかどうかを返します。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    checkArchive
 * Signature: (Ljava/lang/String;I)Z
 */
JNIEXPORT jboolean JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_checkArchive(JNIEnv *env, jobject obj, jstring filename, jint mode) {

    const char *_filename = (*env)->GetStringUTFChars(env, filename, NULL);
    jboolean result = (jboolean) UnGCACheckArchive(_filename, mode);
    (*env)->ReleaseStringUTFChars(env, filename, _filename);

    return result;
}

/**
 * 指定された書庫ファイルに格納されているファイル数を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getFileCount
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getFileCount(JNIEnv *env, jobject obj, jstring filename) {
    const char *_filename = (*env)->GetStringUTFChars(env, filename, NULL);
    int result = (jboolean) UnGCAGetFileCount(_filename);
    (*env)->ReleaseStringUTFChars(env, filename, _filename);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 書庫ファイルを開きます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    openArchive
 * Signature: (Ljava/lang/String;J)V
 */
JNIEXPORT void JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_openArchive(JNIEnv *env, jobject obj, jstring filename, jint mode) {

    mode |= M_ERROR_MESSAGE_OFF;

    const char *_filename = (*env)->GetStringUTFChars(env, filename, NULL);
    HARC _archiveHandle = UnGCAOpenArchive(NULL, _filename, mode);
    (*env)->ReleaseStringUTFChars(env, filename, _filename);

    if (_archiveHandle == NULL) {
        throwExceptionWithIntMessage(env, "java/io/IOException", -1);
    } else {
fprintf(stderr, "handle: %08x\n", (unsigned int) _archiveHandle);
        jclass class = (*env)->GetObjectClass(env, obj);
        jfieldID field = (*env)->GetFieldID(env, class, "instance", "I");
        (*env)->SetIntField(env, obj, field, (jint) _archiveHandle);
    }
}

/**
 * 書庫ファイルを閉じます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    closeArchive
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_closeArchive(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    int result = UnGCACloseArchive((HARC) _archiveHandle);

    if (result != 0) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }
}

/**
 * 最初の格納ファイルの情報を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    findFirst
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_findFirst(JNIEnv *env, jobject obj, jstring key) {

    jint _archiveHandle = getArchiveHandle(env, obj);
//fprintf(stderr, "handle: %d\n", _archiveHandle);

    INDIVIDUALINFO _entry;

    const char *_key = (*env)->GetStringUTFChars(env, key, NULL);
    int result = UnGCAFindFirst((HARC) _archiveHandle, _key, &_entry);
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
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    findNext
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_findNext(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    INDIVIDUALINFO _entry;

    int result = (jboolean) UnGCAFindNext((HARC) _archiveHandle, &_entry);

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
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getCurrentFileName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getCurrentFileName(JNIEnv *env, jobject obj) {
    jint _archiveHandle = getArchiveHandle(env, obj);

    unsigned char _buf[513];

    int result = UnGCAGetFileName((HARC) _archiveHandle, _buf, 512);

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
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getCurrentMethod
 * Signature: ()I;
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getCurrentMethod(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    char _buf[8];

    int result = UnGCAGetMethod((HARC) _archiveHandle, _buf, 8);

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
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getCurrentOriginalSize
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getCurrentOriginalSize(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    unsigned long result = UnGCAGetOriginalSize((HARC) _archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 格納ファイルの圧縮サイズを得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getCurrentCompressedSize
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getCurrentCompressedSize(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    unsigned long result = UnGCAGetCompressedSize((HARC) _archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 格納ファイルの日付を DOS 形式で得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getCurrentDate
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getCurrentDate(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    unsigned int result = UnGCAGetDate((HARC) _archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 格納ファイルの時刻を DOS 形式で得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getCurrentTime
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getCurrentTime(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    unsigned int result = UnGCAGetTime((HARC) _archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 格納ファイルのチェックサムを得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getCurrentCRC
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getCurrentCRC(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    unsigned long result = UnGCAGetCRC((HARC) _archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 検索にマッチしたファイルのサイズの合計を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getSelectedSize
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getSelectedSize(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    long result = UnGCAGetArcOriginalSize((HARC) _archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 検索にマッチしたファイルの圧縮サイズの合計を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getSelectedCompressedSize
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getSelectedCompressedSize(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    long result = UnGCAGetArcCompressedSize((HARC) _archiveHandle);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/**
 * 検索にマッチしたファイルの全体の圧縮率を得ます。
 *
 * Class:     vavi_util_archive_vavi_NativeGcaArchive
 * Method:    getSelectedRatio
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeGcaArchive_getSelectedRatio(JNIEnv *env, jobject obj) {

    jint _archiveHandle = getArchiveHandle(env, obj);

    int result = UnGCAGetArcRatio((HARC) _archiveHandle);
//fprintf(stderr, "result: %d\n", result);

    if (result == -1) {
        throwExceptionWithIntMessage(env, "java/io/IOException", result);
    }

    return result;
}

/* */
