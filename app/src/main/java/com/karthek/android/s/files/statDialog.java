package com.karthek.android.s.files;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.karthek.android.s.files.helper.SFile;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import static android.text.format.Formatter.formatShortFileSize;
import static com.karthek.android.s.files.FApplication.fileType;
import static java.nio.file.Files.walkFileTree;

public class statDialog extends DialogFragment {

	FActivity context;
	TextView textViewSize;
	boolean shouldStop;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		context = (FActivity) getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.frag_d_info, null));
		builder.setPositiveButton(android.R.string.ok, null);
		return builder.create();
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		final SFile sFile = context.selectedFile;
		TextView textView = dialog.findViewById(R.id.FPath);
		textView.setText(sFile.file.getAbsolutePath());
		textView = dialog.findViewById(R.id.textView_date);
		if (sFile.modified == 0) {
			sFile.modified = sFile.file.lastModified();
		}
		textView.setText(new Date(sFile.modified).toString());
		TextView textViewMIME = dialog.findViewById(R.id.textView_MIME);
		TextView textViewMInfo = dialog.findViewById(R.id.textView_MInfo);
		textViewSize = dialog.findViewById(R.id.textView_size);
		if (sFile.isDir) {
			if (sFile.d_traversed) {
				textViewSize.setText(context.getResources().getQuantityString(R.plurals.d_items,
						sFile.d_files,
						sFile.d_dirs, sFile.d_files, formatShortFileSize(context, sFile.d_size)));
			} else {
				new Thread(() -> dSizeCal(sFile)).start();
			}
			dialog.findViewById(R.id.textView_head_MIME).setVisibility(View.GONE);
			dialog.findViewById(R.id.textView_head_MInfo).setVisibility(View.GONE);
			textViewMIME.setVisibility(View.GONE);
			textViewMInfo.setVisibility(View.GONE);
		} else {
			textViewSize.setText(formatShortFileSize(context, sFile.size));
			textViewMIME.setText(sFile.getMimeType());
			textViewMInfo.setText(fileType.getFileMInfo(sFile.file.getAbsolutePath()));
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		shouldStop = true;
		super.onDismiss(dialog);
	}

	long size;
	int dirs = -1;
	int files;

	private void dSizeCal(SFile sfile) {
		try {
			walkFileTree(sfile.file.toPath(), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					dirs++;
					if (shouldStop) return FileVisitResult.TERMINATE;
					textViewSize.post(() -> textViewSize.setText(context.getResources().getQuantityString(R.plurals.d_items, files,
							dirs, files, formatShortFileSize(context,
									size)))

					);
					return super.postVisitDirectory(dir, exc);
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					files++;
					size += attrs.size();
					return super.visitFile(file, attrs);
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!shouldStop) {
			sfile.d_traversed = true;
			sfile.d_dirs = dirs;
			sfile.d_files = files;
			sfile.d_size = size;
		}
	}
}
