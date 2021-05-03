package com.karthek.android.s.files;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class mediaFrag extends Fragment {

	LinearLayout linearLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.frag_media, container, false);
		linearLayout = view.findViewById(R.id.rt_anim_view);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		getSearchFiles();
		/*getVid();
		FApplication.sFiles = new SFile[sFiles.size()];
		for (int s = 0; s < sFiles.size(); s++) {
			FApplication.sFiles[s] = new SFile(new File(sFiles.get(s)));
		}*/
	}

	@Override
	public void onResume() {
		super.onResume();
		linearLayout.animate();
	}

	List<String> sFiles = new ArrayList<>();

	// Container for information about each video.
	static class Video {
		private final Uri uri;
		private final String name;
		private final int duration;
		private final int size;

		public Video(Uri uri, String name, int duration, int size) {
			this.uri = uri;
			this.name = name;
			this.duration = duration;
			this.size = size;
		}
	}

	void getVid() {
		Uri collection;
		collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);

		String[] projection = new String[]{
				MediaStore.Video.Media.RELATIVE_PATH,
				MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.DURATION,
				MediaStore.Video.Media.SIZE
		};
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

		Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
				collection,
				projection,
				selection,
				selectionArgs,
				sortOrder
		);
		// Cache column indices.
		int pathColumn = cursor.getColumnIndex(MediaStore.Video.Media.RELATIVE_PATH);
		int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
		int nameColumn =
				cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
		int durationColumn =
				cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
		int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

		while (cursor.moveToNext()) {
			String path = cursor.getString(pathColumn);
			// Get values of columns for a given video.
			long id = cursor.getLong(idColumn);
			String name = cursor.getString(nameColumn);
			int duration = cursor.getInt(durationColumn);
			int size = cursor.getInt(sizeColumn);

			Uri contentUri = ContentUris.withAppendedId(
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
			String sFile = "/sdcard/" + path;
			if (!sFiles.contains(sFile)) {
				sFiles.add(sFile);
			}
			Log.v("med", String.valueOf(Environment.getDataDirectory()));
		}
		cursor.close();
	}


	void getSearchFiles() {
		List<String> paths = new ArrayList<>();
		Uri uri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
		String[] projection = new String[]{
				MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
				MediaStore.Files.FileColumns.DISPLAY_NAME,
		};

		String selection = MediaStore.Files.FileColumns.DISPLAY_NAME + " LIKE '%pdf'";

		Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri,
				projection, null, null, null);

		int pathColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);
		int nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);

		while (cursor.moveToNext()) {
			String p = cursor.getString(pathColumn);
			if (!paths.contains(p)) {
				paths.add(p);
			}
		}
		for (String s : paths) {
			Log.v("seacrh", s);
		}
	}
}
