package com.karthek.android.s.files;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;

import com.karthek.android.s.files.helper.FileType;

import java.io.File;
import java.io.FileNotFoundException;

import static java.util.Arrays.copyOf;

public class FProvider extends ContentProvider {
	@Override
	public boolean onCreate() {
		System.out.println("calld cr");
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		File file = getFileFromUri(uri);
		if (projection == null) {
			projection = new String[]{
					OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
			};
		}
		String[] cols = new String[projection.length];
		Object[] values = new Object[projection.length];
		int i = 0;
		for (String col : projection) {
			if (OpenableColumns.DISPLAY_NAME.equals(col)) {
				cols[i] = OpenableColumns.DISPLAY_NAME;
				values[i++] = file.getName();
			} else if (OpenableColumns.SIZE.equals(col)) {
				cols[i] = OpenableColumns.SIZE;
				values[i++] = file.length();
			}
		}
		cols = copyOf(cols, i);
		values = copyOf(values, i);
        /*String[] cols = new String[2];
        cols[0] = OpenableColumns.DISPLAY_NAME;
        cols[1]=OpenableColumns.SIZE;
        Object[] values = new Object[2];
        values[0] = file.getName();
        values[1]=file.length();*/
		MatrixCursor cursor = new MatrixCursor(cols, 1);
		cursor.addRow(values);
		/*if (projection != null) {
			for (String p : projection)
				System.out.println(p);
		}*/
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return FileType.getFileMIMEType(getFileFromUri(uri).getAbsolutePath());
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		File file = getFileFromUri(uri);
		return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
	}

	private File getFileFromUri(Uri uri) {
		String path = Uri.decode(uri.getEncodedPath());
        /*final int splitIndex = path.indexOf('/', 1);
        final String tag = Uri.decode(path.substring(1, splitIndex));
        path = Uri.decode(path.substring(splitIndex + 1));*/
		return new File(path);
	}
}
