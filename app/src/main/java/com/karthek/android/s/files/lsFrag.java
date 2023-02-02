package com.karthek.android.s.files;

import static android.content.Intent.ACTION_VIEW;
import static java.nio.file.Files.walkFileTree;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.karthek.android.s.files.helper.DentsLoader;
import com.karthek.android.s.files.helper.SFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class lsFrag extends ListFragment implements LoaderManager.LoaderCallbacks<SFile[]>,
		SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener,
		SearchView.OnQueryTextListener,
		SearchView.OnCloseListener {

	public File Cwd = Environment.getExternalStorageDirectory();
	int NestChop = -1;
	ProgressBar progressBar;
	final Stack<Parcelable> parcelableStack = new Stack<>();
	Parcelable curState;
	LruCache<String, Bitmap> bitmapLruCache;
	SFile[] sFiles;
	public boolean showHidden;
	public int sortType;
	public boolean sortAscending;
	boolean shouldStop = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		int cacheSize = (int) ((Runtime.getRuntime().maxMemory()) / 1024);
		bitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount() / 1024;
			}
		};
		String s = getActivity().getIntent().getStringExtra("p");
		if (s != null) {
			switch (s) {
				case "Download":
					Cwd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
					break;
				case "Recents":

			}
		}
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		showHidden = sharedPreferences.getBoolean("prefs_gen_a", false);
		sortAscending = sharedPreferences.getBoolean("prefs_sort_asc", true);
		switch (sharedPreferences.getString("prefs_sort_type", "File name")) {
			case "File name":
				sortType = 0;
				break;
			case "Size":
				sortType = 1;
				break;
			case "Modified":
				sortType = 2;
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
		m2(viewHolder.sFile);
	}


	@Override
	public void onClick(View v) {
		((FActivity) getActivity()).addDirButton.setVisibility(View.GONE);
	}

	@Override
	public boolean onQueryTextSubmit(final String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(final String newText) {
		sFiles = null;
		if (newText.length() > 1) {
			getListView().setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
			new Thread(() -> {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) getSearchFiles(newText);
				else goSearchFiles(newText);
			}).start();
		}
		return false;
	}

	@Override
	public boolean onClose() {
		((FActivity) getActivity()).addDirButton.setVisibility(View.VISIBLE);
		getLoaderManager().restartLoader(0, null, this);
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
						sortType = 0;
						break;
					case "Size":
						sortType = 1;
						break;
					case "Modified":
						sortType = 2;
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
			m0();
			return true;
		}
		return false;
	}


	private void m0() {
		File file = Cwd;
		curState = parcelableStack.pop();
		Cwd = file.getParentFile();
		NestChop--;
		progressBar.setVisibility(View.VISIBLE);
		getListView().setVisibility(View.GONE);
		getLoaderManager().restartLoader(0, null, this);
	}

	private void m2(SFile sFile) {
		if (sFile.isDir) {
			Cwd = sFile.file;
			System.out.println(Cwd);
			NestChop++;
			getListView().setVisibility(View.GONE);
			getLoaderManager().restartLoader(0, null, this);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			//FArchive.listArchiveEntries(ftmp.getAbsolutePath());
			//FArchive.extractArchive(ftmp.getAbsolutePath());
			Intent intent = new Intent(ACTION_VIEW);
			String FType = sFile.getMimeType();
			intent.setDataAndTypeAndNormalize(Uri.parse("content://"+BuildConfig.APPLICATION_ID+".FProvider" +
					".file/" + Uri.encode(sFile.file.getAbsolutePath())), FType);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			try {
				startActivity(intent);
			} catch (Exception e) {
				if (FType.equals("application/octet-stream")) {
					intent.setDataAndTypeAndNormalize(Uri.parse("content://com.karthek.android.s.files.FProvider" +
							".file/" + Uri.encode(sFile.file.getAbsolutePath())), "*/*");
					startActivity(intent);
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

	private void goSearchFiles(String query) {
		shouldStop = false;
		final ArrayList<SFile> fileArrayList = new ArrayList<>();
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**" + query + "**");
		try {
			walkFileTree(Cwd.toPath(), new SimpleFileVisitor<Path>() {
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
						getActivity().runOnUiThread(() -> {
							((lsAdapter) getListAdapter()).notifyDataSetChanged();
							setListAdapter(getListAdapter());
						});
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@TargetApi(Build.VERSION_CODES.Q)
	private void getSearchFiles(String query) {
		String relPath = "";
		Path CPath = Cwd.toPath();
		if (CPath.getNameCount() > 3) {
			relPath = CPath.subpath(3, CPath.getNameCount()).toString();
		}
		List<SFile> sFileList = new ArrayList<>();
		Uri uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
		String[] projection = new String[]{
				MediaStore.Files.FileColumns.DATA,
				MediaStore.Files.FileColumns.DISPLAY_NAME,
				MediaStore.Files.FileColumns.SIZE,
				MediaStore.Files.FileColumns.DATE_MODIFIED,
				MediaStore.Files.FileColumns.MIME_TYPE,
		};

		String selection =
				MediaStore.Files.FileColumns.RELATIVE_PATH + " LIKE " + "'" + relPath + "%'" + " and" +
						" " + MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE " +
						"'%" + query + "%'";

		Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri,
				projection, selection, null, null);

		int pathColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
		int nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
		int sizeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
		int lmColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED);
		int mimeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE);

		while (cursor.moveToNext()) {
			String path = cursor.getString(pathColumn);
			String name = cursor.getString(nameColumn);
			long size = cursor.getLong(sizeColumn);
			long lm = cursor.getLong(lmColumn) * 1000;
			String mime = cursor.getString(mimeColumn);
			sFileList.add(new SFile(new File(path), size, lm, mime));
		}
		cursor.close();
		sFiles = sFileList.toArray(new SFile[0]);
		getActivity().runOnUiThread(() -> {
			((lsAdapter) getListAdapter()).notifyDataSetChanged();
			setListAdapter(getListAdapter());
			progressBar.setVisibility(View.GONE);
		});
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		bitmapLruCache.put(key, bitmap);
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return bitmapLruCache.get(key);
	}


}
