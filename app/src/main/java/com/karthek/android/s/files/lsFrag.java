package com.karthek.android.s.files;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.karthek.android.s.files.helper.DentsLoader;
import com.karthek.android.s.files.helper.FileType;
import com.karthek.android.s.files.helper.SFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Stack;

import static android.content.Intent.ACTION_VIEW;
import static java.nio.file.Files.walkFileTree;

public class lsFrag extends ListFragment implements LoaderManager.LoaderCallbacks<SFile[]>,
		SharedPreferences.OnSharedPreferenceChangeListener, SearchView.OnQueryTextListener,
		SearchView.OnCloseListener {

	public String Cwd = "/storage/emulated/0/";
	int NestChop = -1;
	ProgressBar progressBar;
	Stack<Parcelable> parcelableStack = new Stack<>();
	Parcelable curState;
	LruCache<String, Bitmap> bitmapLruCache;
	SFile[] sFiles;
	public boolean showHidden;
	public int SType;
	public boolean sortAscending;

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
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		showHidden = sharedPreferences.getBoolean("prefs_gen_a", false);
		sortAscending = sharedPreferences.getBoolean("prefs_sort_asc", true);
		switch (sharedPreferences.getString("prefs_sort_type", "File name")) {
			case "File name":
				SType = 0;
				break;
			case "Size":
				SType = 1;
				break;
			case "Modified":
				SType = 2;
				break;
		}
		getLoaderManager().initLoader(0, null, this);
	}


	@Override
	public void onResume() {
		super.onResume();
		PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
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
		parcelableStack.push(l.onSaveInstanceState());
		lsAdapter.ViewHolder viewHolder = (lsAdapter.ViewHolder) v.getTag();
		m2(viewHolder.sFile.file.getAbsolutePath());
	}

	boolean shouldStop = false;

	@Override
	public boolean onQueryTextSubmit(final String query) {
		shouldStop = !shouldStop;
		sFiles=null;
		getListView().setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					goSearchFiles(query);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		return false;
	}

	@Override
	public boolean onQueryTextChange(final String newText) {
		return false;
	}

	@Override
	public boolean onClose() {
		getLoaderManager().restartLoader(0,null,this);
		return false;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		switch (key) {
			case "prefs_gen_a":
				showHidden = sharedPreferences.getBoolean(key, false);
				break;
			case "prefs_sort_asc":
				sortAscending = sharedPreferences.getBoolean(key, true);
				break;
			case "prefs_sort_type":
				switch (sharedPreferences.getString(key, null)) {
					case "File name":
						Log.v("prefs", key);
						SType = 0;
						break;
					case "Size":
						SType = 1;
						break;
					case "Modified":
						SType = 2;
						break;
				}
				break;
		}
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public Loader<SFile[]> onCreateLoader(int id, Bundle args) {
		return new DentsLoader(getActivity(), this);
	}

	@Override
	public void onLoadFinished(Loader<SFile[]> loader, SFile[] data) {
		System.out.println("on finished task");
		sFiles = data;
		if (NestChop == -1) {
			NestChop = 0;
			lsAdapter lsAdapter = new lsAdapter(this);
			setListAdapter(lsAdapter);
		} else {
			((lsAdapter) getListAdapter()).notifyDataSetChanged();
			setListAdapter(getListAdapter());
			getListView().startLayoutAnimation();
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
			getListView().setVisibility(View.GONE);
			getLoaderManager().restartLoader(0, null, this);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			//FArchive.listArchiveEntries(ftmp.getAbsolutePath());
			//FArchive.extractArchive(ftmp.getAbsolutePath());
			Intent intent = new Intent(ACTION_VIEW);
			String FType = FileType.getFileMIMEType(tmp);
			intent.setDataAndType(Uri.parse("content://com.karthek.android.s.files.FProvider.file/" + Uri.encode(tmp)), FType);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			try {
				startActivity(intent);
			} catch (Exception e) {
				if (ftmp.length() == 0) {
					Toast.makeText(getActivity(), "empty file", Toast.LENGTH_SHORT).show();
				} else {
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
		}
	}

	private void goSearchFiles(String query) throws IOException {
		shouldStop=false;
		final ArrayList<SFile> fileArrayList = new ArrayList<>();
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**" + query + "**");
		walkFileTree(Paths.get("/storage/emulated/0"), new SimpleFileVisitor<Path>() {
			int size = 0;

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (pathMatcher.matches(dir)) {
					System.out.println(dir);
					fileArrayList.add(new SFile(dir.toFile()));
				}
				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (shouldStop) return FileVisitResult.TERMINATE;
				addIt();
				return super.postVisitDirectory(dir, exc);
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (pathMatcher.matches(file)) {
					System.out.println(file);
					fileArrayList.add(new SFile(file.toFile()));
				}
				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) {
				return FileVisitResult.CONTINUE;
			}

			private void addIt() {
				int fsize = fileArrayList.size();
				if (fsize > size) {
					size = fsize;
					sFiles = fileArrayList.toArray(new SFile[0]);
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							((lsAdapter) getListAdapter()).notifyDataSetChanged();
							setListAdapter(getListAdapter());
						}
					});
				}
			}
		});

	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		bitmapLruCache.put(key, bitmap);
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return bitmapLruCache.get(key);
	}


}
