package com.karthek.android.s.files;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class dirDialog extends DialogFragment {

	int FCase;
	protected String renamefile;
	Bundle bundle;
	View view;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		bundle = getArguments();
		if (bundle == null) {
			builder.setMessage("Error");
			return builder.create();
		}
		FCase = bundle.getInt("FCase");
		switch (bundle.getInt("FCase")) {
			case 1:
				builder.setView(inflater.inflate(R.layout.dfrag_fops, null));
				break;
			case 2:
				builder.setView(inflater.inflate(R.layout.frag_d_info, null));
				builder.setPositiveButton(android.R.string.ok,null);
				break;
			case 3:
			case 4:
				builder.setView(inflater.inflate(R.layout.dirdialog, null));
				builder.setTitle(R.string.DescMkdir);
				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						((FActivity) getActivity()).m_fops_mkdir();
					}
				});
				builder.setNegativeButton(android.R.string.cancel, null);
				break;
			case 5:
				builder.setMessage(R.string.play);
				builder.setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("http://play.google.com/store/search?q=" + bundle.getString("FType") + "&c=apps"));
						intent.setPackage("com.android.vending");
						startActivity(intent);
					}
				});
				builder.setNegativeButton(android.R.string.cancel, null);
				break;
			case 6:
				view = inflater.inflate(R.layout.frag_container_prefs, null);
				builder.setView(view);
				break;
			case 7:

		}
		return builder.create();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (FCase == 6) {
			return view;
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}



	@Override
	public void onStart() {
		super.onStart();
		//getDialog().getWindow().setGravity(Gravity.BOTTOM);
		if (bundle == null) {
			System.out.println("gotnull");
		} else {
			this.renamefile = bundle.getString("renamefile");
		}
		switch (FCase) {
			case 1:
				getDialog().getWindow().setGravity(Gravity.BOTTOM);
				break;
			case 4:
				EditText editText = getDialog().findViewById(R.id.editDir);
				editText.setText(renamefile, TextView.BufferType.NORMAL);
				editText.selectAll();
				//InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				// inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				break;
			case 2:
				TextView textView = getDialog().findViewById(R.id.FName);
				assert bundle != null;
				textView.setText(bundle.getString("FName"));
				textView = getDialog().findViewById(R.id.textView_size);
				textView.setText(bundle.getString("size"));
				textView = getDialog().findViewById(R.id.textView_date);
				textView.setText(bundle.getString("MDate"));
				textView = getDialog().findViewById(R.id.textView_MIME);
				textView.setText(bundle.getString("MIMEType"));
				textView = getDialog().findViewById(R.id.textView_MInfo);
				textView.setText(bundle.getString("MInfo"));
				break;
			case 6:
				getChildFragmentManager().beginTransaction()
						.setReorderingAllowed(true)
						.add(R.id.frag_con_view, new PrefsFrag())
						.commit();
				break;
		}
	}
}

