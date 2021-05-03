package com.karthek.android.s.files;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.karthek.android.s.files.helper.FArchive;

import java.io.File;
import java.io.FileNotFoundException;

public class FArchiveActivity extends Activity {

	int fd;
	String targetDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_farchive);
		targetDir = new File(Uri.decode(getIntent().getData().getEncodedPath())).getParent();
		try {
			fd = getContentResolver().openFile(getIntent().getData(), "r",
					null).detachFd();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void m_archive_extract(View view) {
		if (new File(targetDir).exists()) {
			if (FArchive.extractArchive(fd, targetDir) == 0)
				Toast.makeText(this, "extracted", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, "Extraction failed", Toast.LENGTH_SHORT).show();
		}
		this.finish();
	}
}
