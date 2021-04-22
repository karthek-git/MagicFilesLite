package com.karthek.android.s.files;

import android.app.Application;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FApplication extends Application {
	static ExecutorService executorService =
			Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("fapp", "app created");
	}
}
