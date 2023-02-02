package com.karthek.android.s.files;

import android.app.Application;

import com.karthek.android.s.files.helper.FileType;
import com.karthek.android.s.files.helper.SFile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FApplication extends Application {
	static SFile[] sFiles;
	public static FileType fileType;
	static ExecutorService executorService =
			Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);

	@Override
	public void onCreate() {
		super.onCreate();
		fileType = new FileType(this);
	}


}
