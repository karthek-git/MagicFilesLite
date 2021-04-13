package com.karthek.android.s.files.helper;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class DentsLoader extends AsyncTaskLoader<SFile[]> implements FileFilter {

	String Dir;
	boolean showHidden;
	int SType;

	public DentsLoader(Context context, String dir) {
		super(context);
		Dir = dir;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		showHidden = sharedPreferences.getBoolean("prefs_gen_a", false);
	}

	@Override
	public SFile[] loadInBackground() {
		System.out.println("started async" + Dir);
		File[] files;
		SFile[] sFile = null;
		if (!showHidden) {
			files = new File(Dir).listFiles(this);
		} else {
			files = new File(Dir).listFiles();
		}
		if (files != null) {
			sFile = new SFile[files.length];
			for (int s = 0; s < files.length; s++) {
				sFile[s] = new SFile(files[s]);
			}
			Arrays.sort(sFile, new FComparator(0));
		}
		return sFile;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		forceLoad();
	}

	@Override
	public boolean accept(File pathname) {
		return !pathname.isHidden();
	}

}
