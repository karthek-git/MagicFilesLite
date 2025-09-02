package com.karthek.android.s.files;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
		switch (FCase) {
			case 1:
				builder.setView(inflater.inflate(R.layout.dfrag_fops, null));
				break;
			case 3:
			case 4:
				builder.setView(inflater.inflate(R.layout.dirdialog, null));
				builder.setTitle(R.string.DescMkdir);
				builder.setPositiveButton(android.R.string.ok, (dialog, id) -> ((FActivity) getActivity()).m_fops_mkdir());
				builder.setNegativeButton(android.R.string.cancel, null);
				break;
			case 5:
				builder.setMessage(R.string.play);
				builder.setPositiveButton(android.R.string.search_go, (dialog, id) -> {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("http://play.google.com/store/search?q=" + bundle.getString("FType") + "&c=apps"));
					intent.setPackage("com.android.vending");
					startActivity(intent);
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
				Window window = getDialog().getWindow();
				window.setGravity(Gravity.BOTTOM);
				window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				//window.setBackgroundDrawableResource(R.color.colorMainBackGround);
				if (bundle.getBoolean("isDir")) {
					Dialog dialog = getDialog();
					dialog.findViewById(R.id.t_fops_opw).setVisibility(View.GONE);
					dialog.findViewById(R.id.t_fops_opw_sel).setVisibility(View.GONE);
					dialog.findViewById(R.id.t_fops_share).setVisibility(View.GONE);
				}
				break;
			case 4:
				//getDialog().setTitle(R.string.rename);
				EditText editText = getDialog().findViewById(R.id.editDir);
				editText.setText(renamefile, TextView.BufferType.NORMAL);
				editText.selectAll();
				//InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				// inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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

