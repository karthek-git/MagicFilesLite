package com.karthek.android.s.files.helper;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.karthek.android.s.files.FApplication;
import com.karthek.android.s.files.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

public class SFile {
	final public File file;
	public long size = -1;
	public long modified;
	public boolean isDir;
	public boolean d_traversed;
	public long d_size;
	public int d_dirs;
	public int d_files;

	private String mimeType;

	public int res = R.drawable.ic_file_prev;

	public SFile(File file) {
		this.file = file;
		if (file.isDirectory()) isDir = true;
		else size = file.length();
	}

	public SFile(File file, long modified) {
		this.file = file;
		if (file.isDirectory()) isDir = true;
		else size = file.length();
		this.modified = modified;
	}

	public SFile(File file, long size, long modified, String mime) {
		this.file = file;
		if (mime == null && file.isDirectory()) isDir = true;
		else this.size = size;
		this.modified = modified;
	}

	public void initDir() {
		try {
			size = Objects.requireNonNull(file.list()).length;
		} catch (Exception e) {
			e.printStackTrace();
			size = 0;
		}
	}

	public String getMimeType() {
		if (mimeType == null)
			mimeType = FApplication.fileType.getFileMIMEType(file.getAbsolutePath());
		return mimeType;
	}

	public static int getNativeFD(String filename) {
		ParcelFileDescriptor parcelFileDescriptor = null;
		try {
			parcelFileDescriptor = ParcelFileDescriptor.open(new File(filename), ParcelFileDescriptor.MODE_READ_ONLY);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.v("natic", filename);
		}
		return parcelFileDescriptor != null ? parcelFileDescriptor.detachFd() : -1;
	}

	@Override
	public String toString() {
		return "SFile{file=" + file.getAbsolutePath() + '}';
	}
}
