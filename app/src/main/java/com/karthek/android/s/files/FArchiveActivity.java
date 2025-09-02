package com.karthek.android.s.files;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.karthek.android.s.files.helper.FArchive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FArchiveActivity extends Activity {

	int fd;
	String targetDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_farchive);
		final File file = new File(Uri.decode(getIntent().getData().getEncodedPath()));
		targetDir = file.getParent() + "/" + getFileName(file.getName());
		try {
			fd = getContentResolver().openFile(getIntent().getData(), "r",
					null).detachFd();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFileName(String name) {
		int lastIndex = name.lastIndexOf('.');
		Log.v("sub",name.substring(0,lastIndex-1));
		return name.substring(0, lastIndex);
	}

	public void m_archive_extract(View view) {
		try {
			Files.createDirectory(Paths.get(targetDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (new File(targetDir).exists()) {
			if (new FArchive().extractArchive(fd, targetDir) == 0)
				Toast.makeText(this, "extracted", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, "Extraction failed", Toast.LENGTH_SHORT).show();
		}
		this.finish();
	}
}
