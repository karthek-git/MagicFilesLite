package com.karthek.android.s.files.helper;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.karthek.android.s.files.lsFrag;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class DentsLoader extends AsyncTaskLoader<SFile[]> implements FileFilter {

	String Dir;
	boolean showHidden;
	int SType;
	boolean sortAscending;
	boolean loaded;

	public DentsLoader(Context context, lsFrag lsFrag) {
		super(context);
		Dir = lsFrag.Cwd;
		showHidden = lsFrag.showHidden;
		SType = lsFrag.SType;
		sortAscending = lsFrag.sortAscending;
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
			Arrays.sort(sFile, new FComparator(SType, sortAscending));
		}
		loaded = true;
		return sFile;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if (!loaded) forceLoad();
	}

	@Override
	public boolean accept(File pathname) {
		return !pathname.isHidden();
	}

}
