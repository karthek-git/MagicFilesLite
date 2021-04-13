package com.karthek.android.s.files;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PrefsFrag extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
