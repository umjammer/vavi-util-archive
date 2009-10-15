/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

#include <jni.h>
#include "vavi_util_archive_vavi_NativeStuffItArchive.h"
#include <stdio.h>
#include "stuffit5/Error.h"
#include "stuffit5/Reader.h"
#include "stuffit5/FileInfo.h"
#include "stuffit5/FolderInfo.h"
#include "stuffit5/event/FileInfoListener.h"
#include "stuffit5/event/FolderInfoListener.h"
#include "stuffit5/event/FileDecodeBeginListener.h"
#include "stuffit5/event/FileDecodeEndListener.h"
#include "stuffit5/Version.h"
#include "hashtable.h"


/*
 * StuffIt ラッパ
 *
 * @author    <a href=mailto:vavivavi@yahoo.co.jp>nsano</a>
 * @version    0.00    060108    nsano    initial version <br>
 */

//-----------------------------------------------------------------------------

/**
 * アーカイブのハンドルを取得します。
 */
static stuffit5_Reader getArchiveHandle(JNIEnv *env, jobject obj) {
    jclass class = (*env)->GetObjectClass(env, obj);
    jfieldID field = (*env)->GetFieldID(env, class, "instance", "I");
    return (stuffit5_Reader) (*env)->GetIntField(env, obj, field);
}

/**
 * 例外を投げます。
 * @param exception "java/lang/Exception"
 */
static void throwExceptionWithStringMessage(JNIEnv *env, char *exception, char *_message) {

    jclass class = (*env)->FindClass(env, exception);
    (*env)->ThrowNew(env, class, _message);
}

//-----------------------------------------------------------------------------

/**
 * Class:     vavi_util_archive_vavi_NativeStuffItArchive
 * Method:    exec
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_vavi_util_archive_vavi_NativeStuffItArchive_exec
  (JNIEnv *env, jobject obj, jstring command) {

    stuffit5_Reader _archiveHandle = getArchiveHandle(env, obj);

    bool result = stuffit5_Reader_decode(_archiveHandle);
    if (result == false) {
        throwExceptionWithStringMessage(env, "java/io/IOException",  (char *) stuffit5_Error_description(stuffit5_Reader_getError(_archiveHandle)));
        return;
    }
}

/**
 * Class:     vavi_util_archive_vavi_NativeStuffItArchive
 * Method:    getVersion
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_vavi_util_archive_vavi_NativeStuffItArchive_getVersion
  (JNIEnv *env, jobject obj) {
    return (jint) stuffit5_Version_majorv() * 100 + stuffit5_Version_minorv();
}

/** value for map */
struct Value {
    JNIEnv *env;
    jobject obj;
};

/** Map<Long, Value> */
static struct hashtable *instances;

/** hashCode() for key */
static unsigned int keyHashCode(stuffit5_Reader k) {
//fprintf(stderr, "keyHashCode: %d\n", (unsigned int) k);
//fflush(stderr);
    return (unsigned int) k;
}

/** equals() for key */
static int keyEquals(stuffit5_Reader key1, stuffit5_Reader key2) {
//fprintf(stderr, "keyEquals: %d, %d\n", (unsigned int) key1, (unsigned int) key2);
//fflush(stderr);
    return ((int) key1 - (int) key2) == 0;
}

/** */
static bool fileInfoEvent(uint32_t position, stuffit5_Reader reader) {
    struct Value* value = (struct Value*) hashtable_search(instances, reader);
    if (value == NULL) {
fprintf(stderr, "hashtable_search: %d\n", (unsigned int) reader);
fflush(stderr);
        return false;
    }
    JNIEnv *env = value->env;
    jobject obj = value->obj;

    jclass class = (*env)->GetObjectClass(env, obj);
    jmethodID method = (*env)->GetMethodID(env, class, "addFileEntry", "(Ljava/lang/String;JIIII)V");

    //
    stuffit5_FileInfo finfo = stuffit5_Reader_fileInfo(reader);
//fprintf(stderr, "file: %s\n", stuffit5_FileInfo_getName(finfo));
//fflush(stdin);
    const char* _name = stuffit5_FileInfo_getName(finfo);
    long _time = 0;
    int _compressedSize = stuffit5_FileInfo_getCompressedSize(finfo);
    int _size = stuffit5_FileInfo_getUncompressedSize(finfo);

    jstring name = (*env)->NewStringUTF(env, _name);
    (*env)->CallVoidMethod(env, obj, method, name, (jlong) _time, (jint) _size, (jint) _compressedSize, (jint) 0, (jint) 0);

    return true;
}

/** */
static bool folderInfoEvent(uint32_t position, stuffit5_Reader reader) {
    struct Value* value = (struct Value*) hashtable_search(instances, reader);
    if (value == NULL) {
fprintf(stderr, "hashtable_search: %d\n", (unsigned int) reader);
fflush(stderr);
        return false;
    }
    JNIEnv *env = value->env;
    jobject obj = value->obj;

    jclass class = (*env)->GetObjectClass(env, obj);
    jmethodID method = (*env)->GetMethodID(env, class, "addDirectoryEntry", "(Ljava/lang/String;J)V");

    //
    stuffit5_FolderInfo finfo = stuffit5_Reader_folderInfo(reader);
//fprintf(stderr, "folder: %s\n", stuffit5_FolderInfo_getName(finfo));
//fflush(stdin);
    const char* _name = stuffit5_FolderInfo_getName(finfo);
    long _time = 0;

    jstring name = (*env)->NewStringUTF(env, _name);
    (*env)->CallVoidMethod(env, obj, method, name, (jlong) _time);

    return true;
}

/**
 * Class:     vavi_util_archive_vavi_NativeStuffItArchive
 * Method:    openArchive
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_vavi_util_archive_vavi_NativeStuffItArchive_openArchive
  (JNIEnv *env, jobject obj, jstring filename, jint mode) {

    if (instances == NULL) {
        instances = create_hashtable(16, keyHashCode, keyEquals);
    }

    //
    stuffit5_Reader _archiveHandle = stuffit5_Reader_new();
    if (_archiveHandle == (void *) NULL) {
        throwExceptionWithStringMessage(env, "java/io/IOException", "new");
        return;
    }
//fprintf(stderr, "handle: %d (%d)\n", (unsigned int) _archiveHandle, sizeof(_archiveHandle));
//fflush(stderr);
    jclass class = (*env)->GetObjectClass(env, obj);
    jfieldID field = (*env)->GetFieldID(env, class, "instance", "I");
    (*env)->SetIntField(env, obj, field, (jint) _archiveHandle);

    struct Value value;
    value.env = env;
    value.obj = obj;
    if (!hashtable_insert(instances, _archiveHandle, &value)) {
        stuffit5_Reader_delete(_archiveHandle);
        throwExceptionWithStringMessage(env, "java/lang/IllegalStateException", "hashtable_insert");
        return;
    }
//fprintf(stderr, "value: %d\n", (unsigned int)  &value);
//fflush(stderr);

    const char *_filename = (*env)->GetStringUTFChars(env, filename, NULL);
    bool result = stuffit5_Reader_open(_filename, _archiveHandle);
    (*env)->ReleaseStringUTFChars(env, filename, _filename);
    if (result == false) {
        throwExceptionWithStringMessage(env, "java/io/IOException", (char *) stuffit5_Error_description(stuffit5_Reader_getError(_archiveHandle)));
        stuffit5_Reader_delete(_archiveHandle);
        return;
    }

    stuffit5_Reader_addFileInfoListener(fileInfoEvent, _archiveHandle);
    stuffit5_Reader_addFolderInfoListener(folderInfoEvent, _archiveHandle);

    if (stuffit5_Reader_scan(_archiveHandle) == false) {
        stuffit5_Reader_close(_archiveHandle);
        throwExceptionWithStringMessage(env, "java/io/IOException", (char *) stuffit5_Error_description(stuffit5_Reader_getError(_archiveHandle)));
        stuffit5_Reader_delete(_archiveHandle);
        return;
    }
}

/**
 * Class:     vavi_util_archive_vavi_NativeStuffItArchive
 * Method:    closeArchive
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_vavi_util_archive_vavi_NativeStuffItArchive_closeArchive
  (JNIEnv *env, jobject obj) {

    hashtable_destroy(instances, 0);

    stuffit5_Reader _archiveHandle = getArchiveHandle(env, obj);

    bool result = stuffit5_Reader_close(_archiveHandle);
    if (result == false) {
        throwExceptionWithStringMessage(env, "java/io/IOException",  (char *) stuffit5_Error_description(stuffit5_Reader_getError(_archiveHandle)));
        return;
    }

    stuffit5_Reader_delete(_archiveHandle);
}

//-----------------------------------------------------------------------------

/** */
static bool fileInfoEvent_main(uint32_t position, stuffit5_Reader reader) {
    stuffit5_FileInfo finfo = stuffit5_Reader_fileInfo(reader);
printf("file: %s\n", stuffit5_FileInfo_getName(finfo));
    return true;
}

/** */
static bool folderInfoEvent_main(uint32_t position, stuffit5_Reader reader) {
    stuffit5_FolderInfo finfo = stuffit5_Reader_folderInfo(reader);
printf("folder: %s\n", stuffit5_FolderInfo_getName(finfo));
    return true;
}

/** */
static bool fileDecodeBeginEvent_main(uint32_t position, stuffit5_Reader reader) {
    stuffit5_FileInfo finfo = stuffit5_Reader_fileInfo(reader);
printf("file: %s\n", stuffit5_FileInfo_getName(finfo));
    return true;
}

/** */
static void fileDecodeEndEvent_main(stuffit5_Reader reader) {
    stuffit5_FolderInfo finfo = stuffit5_Reader_folderInfo(reader);
printf("folder: %s\n", stuffit5_FolderInfo_getName(finfo));
}

/**
 * test
 */
int main(int argc, char** argv) {
    int i;
    stuffit5_Reader reader;

    fprintf(stderr, "StuffIt Engine %s\n", stuffit5_Version_readable());
    if (argc <= 1) {
        printf("Usage: expand2 (files...)\n");
        return 1;
    }

    reader = stuffit5_Reader_new();
    if (reader == 0) {
        fprintf(stderr, "Cannot create an stuffit5_Reader\n");
        return 2;
    }

    for (i = 1; i < argc; i++) {
        fprintf(stderr, "    Expanding %s...\n", argv[i]);
        if (stuffit5_Reader_open(argv[i], reader) == false) {
            stuffit5_Reader_delete(reader);
            fprintf(stderr, "Cannot open %s\n", argv[i]);
            return 3;
        }

        stuffit5_Reader_addFileInfoListener(fileInfoEvent_main, reader);
        stuffit5_Reader_addFolderInfoListener(folderInfoEvent_main, reader);

        if (stuffit5_Reader_scan(reader) == false) {
            fprintf(stderr, "Cannot scan %s: %s\n", argv[i], stuffit5_Error_description(stuffit5_Reader_getError(reader)));
        }

        stuffit5_Reader_addFileDecodeBeginListener(fileDecodeBeginEvent_main, reader);
        stuffit5_Reader_addFileDecodeEndListener(fileDecodeEndEvent_main, reader);

//        if (stuffit5_Reader_decode(reader) == false) {
//            fprintf(stderr, "Cannot decode %s: %s\n", argv[i], stuffit5_Error_description(stuffit5_Reader_getError(reader)));
//        }

        stuffit5_Reader_close(reader);
    }

    stuffit5_Reader_delete(reader);

    fprintf(stderr, "All OK\n");
    return 0;
}

/* */
