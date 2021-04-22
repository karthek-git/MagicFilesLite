package com.karthek.android.s.files;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().setHideOnContentScrollEnabled(true);
		return true;
	}

	public void fic(View view) {
		Intent intent = new Intent(this, FActivity.class);
		intent.putExtra("p", "/storage/emulated/0/");
		startActivity(intent);
	}

	public void m_storage_analyze(View view) {
		Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
		startActivity(intent);
	}
}
