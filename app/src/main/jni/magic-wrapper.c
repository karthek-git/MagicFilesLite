//
// Created by karthik on 3/21/21.
//
#include <jni.h>
#include <stdlib.h>
#include <syslog.h>
#include <pthread.h>
#include <include/magic.h>
#include <include/archive.h>
#include <include/archive_entry.h>

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>


jobject asset_g_ref;

JNIEXPORT jlong JNICALL
Java_com_karthek_android_s_files_helper_FileType_c_1magic_1open(JNIEnv *env, jclass clazz,
																jobject asset_manager) {
	magic_t magic = magic_open(MAGIC_MIME_TYPE);
	//magic_load(magic, "/data/user/0/com.karthek.android.s.files/files/magic.mgc");
	asset_g_ref = (*env)->NewGlobalRef(env, asset_manager);
	AAsset *asset = AAssetManager_open(AAssetManager_fromJava(env, asset_g_ref), "magic.mgc",
									   O_RDONLY);
	const void *buffers[] = {AAsset_getBuffer(asset)};
	size_t sizes[] = {AAsset_getLength(asset)};
	magic_load_buffers(magic, (void **) buffers, sizes, 1);
	return (long) magic;
}

#ifdef NATIVE_SYNC
pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;
#endif

JNIEXPORT jstring JNICALL
Java_com_karthek_android_s_files_helper_FileType_c_1magic_1descriptor(JNIEnv *env, jobject thiz,
																	  jlong cookie, jint fd) {
#ifdef NATIVE_SYNC
	pthread_mutex_lock(&mtx);
#endif
	jstring ret = (*env)->NewStringUTF(env, magic_descriptor((magic_t) cookie, fd));
	close(fd);
#ifdef NATIVE_SYNC
	pthread_mutex_unlock(&mtx);
#endif
	return ret;
}

JNIEXPORT void JNICALL
Java_com_karthek_android_s_files_helper_FileType_c_1magic_1setflags(JNIEnv *env, jobject thiz,
																	jlong cookie,
																	jint flag) {
	if (flag == 1) {
		magic_setflags((magic_t) cookie, 0);
	} else {
		magic_setflags((magic_t) cookie, MAGIC_MIME_TYPE);
	}
}

JNIEXPORT jstring JNICALL
Java_com_karthek_android_s_files_helper_FileType_c_1magic_1error(JNIEnv *env, jclass clazz,
																 jlong magic_cookie) {
	return (*env)->NewStringUTF(env, magic_error((magic_t) magic_cookie));
}


struct archive *a;
struct archive_entry *entry;
int r;

JNIEXPORT jlong JNICALL
Java_com_karthek_android_s_files_helper_FArchive_c_1archive_1list(JNIEnv *env, jclass clazz,
																  jint fd) {
	a = archive_read_new();
	archive_read_support_filter_all(a);
	archive_read_support_format_all(a);
	r = archive_read_open_fd(a, fd, 10240);
	if (r != ARCHIVE_OK) {
		syslog(LOG_INFO, "%s\n", archive_error_string(a));
		return (1);
	}
	while (archive_read_next_header(a, &entry) == ARCHIVE_OK) {
		syslog(LOG_INFO, "f:%s\n", archive_entry_pathname(entry));
		archive_read_data_skip(a);
	}
	r = archive_read_free(a);
	if (r != ARCHIVE_OK)
		return (1);
	return 0;
}


JNIEXPORT jint JNICALL
Java_com_karthek_android_s_files_helper_FArchive_c_1archive_1extract(JNIEnv *env, jclass clazz,
																	 jint fd, jstring target) {
	a = archive_read_new();
	archive_read_support_filter_all(a);
	archive_read_support_format_all(a);
	r = archive_read_open_fd(a, fd, 10240);
	if (r != ARCHIVE_OK) {
		syslog(LOG_INFO, "err: %s\n", archive_error_string(a));
		return (1);
	}
	if (chdir((*env)->GetStringUTFChars(env, target, NULL)) != 0)
		return -1;
	int flags = ARCHIVE_EXTRACT_TIME;
	flags |= ARCHIVE_EXTRACT_PERM;
	flags |= ARCHIVE_EXTRACT_ACL;
	flags |= ARCHIVE_EXTRACT_FFLAGS;

	for (;;) {
		r = archive_read_next_header(a, &entry);
		if (r == ARCHIVE_EOF)
			break;
		archive_read_extract(a, entry, flags);
		if (r != ARCHIVE_OK) {
			syslog(LOG_INFO, "err: %s\n", archive_error_string(a));
			return r;
		}
	}

	r = archive_read_free(a);
	if (r != ARCHIVE_OK)
		return (1);
	return 0;
}