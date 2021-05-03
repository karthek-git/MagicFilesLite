package com.karthek.android.s.files;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

public class sBottomDialog extends Dialog {

	Window window;


	public sBottomDialog(Context context) {
		super(context);
		window=getWindow();
		setContentView(R.layout.dfrag_fops);
		getWindow().setGravity(Gravity.BOTTOM);
	}

	public sBottomDialog(Context context, int themeResId) {
		super(context, themeResId);
		window=getWindow();
		setContentView(R.layout.dfrag_fops);
		getWindow().setGravity(Gravity.BOTTOM);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		window.setContainer(null);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}
}
