package com.karthek.android.s.files;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.karthek.android.s.files.helper.DentsLoader;
import com.karthek.android.s.files.helper.FileType;
import com.karthek.android.s.files.helper.SFile;

import java.io.File;
import java.util.Stack;

import static android.content.Intent.ACTION_VIEW;

public class lsFrag extends ListFragment implements LoaderManager.LoaderCallbacks<SFile[]> {

	String Cwd = "/storage/emulated/0/";
	int NestChop = 0;
	ProgressBar progressBar;
	Stack<Parcelable> parcelableStack = new Stack<>();
	Parcelable curState;
	LruCache<String, Bitmap> bitmapLruCache;
	SFile[] sFiles;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int cacheSize = (int) ((Runtime.getRuntime().maxMemory()) / 1024);
		bitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
		String s = getActivity().getIntent().getStringExtra("p");
		if (s != null) {
			Cwd = s;
		}
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.frag_list, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(android.R.id.empty).setVisibility(View.GONE);
		progressBar = view.findViewById(android.R.id.progress);
		//setEmptyText("no data");
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		parcelableStack.push(getListView().onSaveInstanceState());
		lsAdapter.ViewHolder viewHolder = (lsAdapter.ViewHolder) v.getTag();
		m2(Cwd + viewHolder.FileName.getText());
	}


	@Override
	public Loader<SFile[]> onCreateLoader(int id, Bundle args) {
		return new DentsLoader(getActivity(), Cwd);
	}

	@Override
	public void onLoadFinished(Loader<SFile[]> loader, SFile[] data) {
		System.out.println("on finished task");
		sFiles = data;
		if (NestChop == 0) {
			lsAdapter lsAdapter = new lsAdapter(this);
			setListAdapter(lsAdapter);
		} else {
			((lsAdapter) getListAdapter()).notifyDataSetChanged();
		}
		if (curState != null) {
			getListView().onRestoreInstanceState(curState);
			curState = null;
		}
		if (data == null || data.length == 0) {
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<SFile[]> loader) {

	}

	public boolean xBackPressed() {
		if (NestChop != 0) {
			m0(0);
			return true;
		}
		return false;
	}


	public void m0(int flg) {
		System.out.println("m0:" + Cwd);
		//findViewById(R.id.fops_overlay).setVisibility(View.GONE);
		File file = new File(Cwd);
		String tmp;
		if (flg == 0) {
			curState = parcelableStack.pop();
			file = file.getParentFile();
		}
		if (file != null) {
			tmp = file.getAbsolutePath();
			NestChop--;
			Cwd = tmp + "/";
			progressBar.setVisibility(View.VISIBLE);
			getListView().setVisibility(View.GONE);
			getLoaderManager().restartLoader(0, null, this);
		}
	}

	public void m2(String tmp) {
		File ftmp = new File(tmp);
		if (ftmp.isDirectory()) {
			Cwd = tmp + "/";
			System.out.println(Cwd);
			NestChop++;
			progressBar.setVisibility(View.VISIBLE);
			getListView().setVisibility(View.GONE);
			getLoaderManager().restartLoader(0, null, this);
		} else if (ftmp.isFile()) {
			//FArchive.listArchiveEntries(ftmp.getAbsolutePath());
			//FArchive.extractArchive(ftmp.getAbsolutePath());
			Intent intent = new Intent(ACTION_VIEW);
			if (ftmp.length() == 0) {
				Toast.makeText(getActivity(), "empty file", Toast.LENGTH_SHORT).show();
			} else {
				String FType = FileType.getFileMIMEType(tmp);
				intent.setDataAndType(Uri.parse("content://com.karthek.android.s.files.FProvider.file/" + Uri.encode(tmp)), FType);
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
				try {
					startActivity(intent);
				} catch (Exception e) {
					Bundle bundle = new Bundle();
					bundle.putInt("FCase", 5);
					bundle.putString("FType", FType);
					dirDialog dirDialog = new dirDialog();
					dirDialog.setArguments(bundle);
					dirDialog.show(getChildFragmentManager(), "play");
					//Toast.makeText(this, "No App Found to handle this FileType", Toast.LENGTH_SHORT).show();
					//e.printStackTrace();
				}
			}
		} else {
			System.out.println(tmp);
		}
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			bitmapLruCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return bitmapLruCache.get(key);
	}
}
