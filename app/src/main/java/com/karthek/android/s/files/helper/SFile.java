package com.karthek.android.s.files.helper;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.karthek.android.s.files.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

public class SFile {
	public File file;
	public long size;
	public long modified;
	public boolean isDir;

	public int res= R.drawable.ic_file_prev;

	public SFile(File file) {
		this.file = file;
		if(file.isDirectory()){
			isDir=true;
			try {
				size= Objects.requireNonNull(file.list()).length;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			size = file.length();
		}
		modified=file.lastModified();
	}

	public static int getNativeFD(String filename){
		ParcelFileDescriptor parcelFileDescriptor=null;
		try {
			parcelFileDescriptor = ParcelFileDescriptor.open(new File(filename),ParcelFileDescriptor.MODE_READ_ONLY );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.v("natic", filename);
		}
		return parcelFileDescriptor != null ? parcelFileDescriptor.detachFd() : -1;
	}
}
