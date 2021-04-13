package com.karthek.android.s.files;


import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.karthek.android.s.files.helper.FileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Intent.ACTION_SEND;
import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
import static android.text.format.Formatter.formatShortFileSize;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FActivity extends Activity implements AbsListView.OnScrollListener, AbsListView.MultiChoiceModeListener, Toolbar.OnMenuItemClickListener {

	lsFrag listFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_f);
		System.out.println("called oncreate\n");
		String[] perms = new String[1];
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			if (!Environment.isExternalStorageManager()) {
				startActivity(new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).setData(Uri.parse("package:com.karthek.android.s.files")));
			}
		} else {
			perms[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
			requestPermissions(perms, 1);
		}
	}

	int grant = 0;

	public static String selectedFile;

	final List<String> SelectedFileList = new ArrayList<>();
	ListView lsView;

	public void m4() {
		lsView = listFragment.getListView();
		lsView.setOnScrollListener(this);
		lsView.setMultiChoiceModeListener(this);
	}


	@Override
	protected void onStart() {
		listFragment = (lsFrag) getFragmentManager().findFragmentById(R.id.frag_lsView);
		super.onStart();
		Toolbar toolbar = findViewById(R.id.t_fops);
		//getMenuInflater().inflate(R.menu.t_fops, toolbar.getMenu());
		toolbar.setOnMenuItemClickListener(this);
		System.out.println("called onstart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			if (Environment.isExternalStorageManager()) m4();
		}
		if (!new File(getFilesDir(), "magic.mgc").exists()) {
			try {
				Files.copy(getAssets().open("magic.mgc"), Paths.get(this.getFilesDir().toString(), "magic.mgc"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("called onresume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("called onpause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("called onstop");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		System.out.println("called onrestart");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("called ondestroy");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.app_bar, menu);

		SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
		SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(ComponentName.createRelative(this, ".SearchActivity")));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		System.out.println("gttt");
		int id = item.getItemId();
		if (id == R.id.ab_paste) {
			try {
				t_fops_paste();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (id == R.id.ab_customize) {
			Bundle bundle = new Bundle();
			bundle.putInt("FCase", 6);
			dirDialog dirDialog = new dirDialog();
			dirDialog.setArguments(bundle);
			dirDialog.show(getFragmentManager(), "play");
		} else {
			System.out.println(onSearchRequested());
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.findItem(R.id.ab_paste);
		if (!(menuItem.isVisible()) & Ccp) {
			System.out.println("onpreppp");
			menuItem.setVisible(true);
			Ccp = false;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (!(listFragment.xBackPressed())) {
			super.onBackPressed();
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			grant = 1;
			m4();
		} else {
			Toast.makeText(this, "GRANT PERMISSION TO ACCESS FILES", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		View view1 = findViewById(R.id.rel_new_dir);
		if (scrollState == SCROLL_STATE_IDLE) {
			view1.setVisibility(View.VISIBLE);
		} else {
			view1.setVisibility(View.INVISIBLE);
		}
		//System.out.println("scrolling");
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//findViewById(R.id.rel_new_dir).setVisibility(View.VISIBLE);
		//System.out.println("scrolled");
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		if (checked) {
			SelectedFileList.add(((File) lsView.getItemAtPosition(position)).getAbsolutePath());
		} else {
			SelectedFileList.remove(((File) lsView.getItemAtPosition(position)).getAbsolutePath());
		}
		System.out.println("checked " + position + " " + checked);
		mode.setSubtitle("" + lsView.getCheckedItemCount());
		System.out.println(SelectedFileList);
	}

	ActionMode actionMode;

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		actionMode = mode;
		mode.setTitle("Selected");
		//getMenuInflater().inflate(R.menu.app_bar, menu);
		Toolbar toolbar = findViewById(R.id.t_fops);
		toolbar.setVisibility(View.VISIBLE);
		System.out.println("ac created");
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		System.out.println("ac prepd");
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		System.out.println("ac cld");
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		if (!Ccp) {
			SelectedFileList.clear();
		}
		findViewById(R.id.t_fops).setVisibility(View.GONE);
		System.out.println("ac destroyed");
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return true;
	}


	public void m_fops(View view) {
		lsAdapter.ViewHolder viewHolder = (lsAdapter.ViewHolder) ((View) view.getParent()).getTag();
		selectedFile = viewHolder.FileName.getText().toString();
		System.out.println(selectedFile);
		Bundle bundle = new Bundle();
		bundle.putInt("FCase", 1);
		dirdialog = new dirDialog();
		dirdialog.setArguments(bundle);
		dirdialog.show(getFragmentManager(), "dia");
	}

	int clp_org = 0;
	boolean Ccp;

	public void m_fops_yks(View view) {
		if (view.getId() == R.id.t_fops_ynk) {
			clp_org = 1;
		} else {
			clp_org = 0;
		}
		if (selectedFile != null) {
			SelectedFileList.add(listFragment.Cwd + selectedFile);
			selectedFile = null;
		}
		Ccp = true;
		if (actionMode != null) {
			actionMode.finish();
			invalidateOptionsMenu();
		}
	}

	public void m_fops_del(View view) {
		if (selectedFile != null) {
			SelectedFileList.add(listFragment.Cwd + selectedFile);
			selectedFile = null;
		}
		for (String SFile : SelectedFileList) {
			try {
				if (new File(SFile).isDirectory()) {
					walkFileTree(Paths.get(SFile), new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
								throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException e)
								throws IOException {
							if (e == null) {
								Files.delete(dir);
								return FileVisitResult.CONTINUE;
							} else {
								throw e;
							}
						}
					});
				} else {
					Files.deleteIfExists(Paths.get(SFile));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		SelectedFileList.clear();
		if (actionMode != null) {
			actionMode.finish();
		} else {
			listFragment.getLoaderManager().restartLoader(0, null, listFragment);
			dirdialog.dismiss();
		}
	}


	public void m_fops_share(View view) {
		Intent intent = new Intent(ACTION_SEND);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://com.karthek.android.s.files.FProvider.file" + Uri.encode(listFragment.Cwd, selectedFile)));
		intent.setType(FileType.getFileMIMEType(listFragment.Cwd + selectedFile));
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		startActivity(Intent.createChooser(intent, null));
	}

	dirDialog dirdialog;

	public void m_fops_adddir(View view) {
		Bundle bundle = new Bundle();
		bundle.putInt("FCase", 3);
		dirdialog = new dirDialog();
		dirdialog.setArguments(bundle);
		dirdialog.show(getFragmentManager(), "dia");
	}

	int renm = 0;

	public void m_fops_mkdir() {
		EditText editText = dirdialog.getDialog().findViewById(R.id.editDir);
		if (renm == 1) {
			File file = new File(listFragment.Cwd + selectedFile);
			File dest = new File(listFragment.Cwd + editText.getText());
			if (!(file.renameTo(dest))) {
				Toast.makeText(this, "Unable to Rename", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, selectedFile + " renamed to " + editText.getText(), Toast.LENGTH_SHORT).show();
			}
		} else {
			try {
				Files.createDirectory(Paths.get(listFragment.Cwd, String.valueOf(editText.getText())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		listFragment.getLoaderManager().restartLoader(0, null, listFragment);

	}

	public void m_fops_editname(View view) {
		renm = 1;
		Bundle bundle = new Bundle();
		bundle.putInt("FCase", 4);
		bundle.putString("renamefile", selectedFile);
		System.out.println(bundle.getString("renamefile"));
		dirdialog.dismiss();
		dirdialog = new dirDialog();
		//noinspection deprecation
		dirdialog.setArguments(bundle);
		//noinspection deprecation
		dirdialog.show(getFragmentManager(), "rename");
	}

	@SuppressWarnings("deprecation")
	public void m_fops_stat(View view) {
		//findViewById(R.id.fops_overlay).setVisibility(View.GONE);
		File file = new File(listFragment.Cwd + selectedFile);
		Bundle bundle = new Bundle();
		bundle.putString("FName", file.getName());
		bundle.putString("size", formatShortFileSize(this, file.length()));
		bundle.putString("MDate", new Date(file.lastModified()).toString());
		bundle.putString("MIMEType", FileType.getFileMIMEType(file.getAbsolutePath()));
		bundle.putString("MInfo", FileType.getFileMInfo(file.getAbsolutePath()));
		bundle.putInt("FCase", 2);
		dirdialog = new dirDialog();
		dirdialog.setArguments(bundle);
		dirdialog.show(getFragmentManager(), "info");
	}

	public void t_fops_paste() throws IOException {
		if (clp_org == 1) {
			for (String SFile : SelectedFileList) {
				Path path = Paths.get(SFile);
				if (new File(SFile).isDirectory()) {
					t_fops_paste_dir(path, Paths.get(listFragment.Cwd, String.valueOf(path.getFileName())), true);
				} else {
					Files.copy(path, Paths.get(listFragment.Cwd, String.valueOf(path.getFileName())), REPLACE_EXISTING);
				}
			}
		} else {
			for (String SFile : SelectedFileList) {
				Path path = Paths.get(SFile);
				if (new File(SFile).isDirectory()) {
					t_fops_paste_dir(path, Paths.get(listFragment.Cwd, String.valueOf(path.getFileName())), false);
				} else {
					Files.move(path, Paths.get(listFragment.Cwd, String.valueOf(path.getFileName())), REPLACE_EXISTING);
				}
			}
		}
		//m0(1);
		listFragment.getLoaderManager().restartLoader(0, null, listFragment);

	}

	private void t_fops_paste_dir(final Path source, final Path target, final boolean org) throws IOException {
		walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (org) {
					Files.copy(file, target.resolve(source.relativize(file)), REPLACE_EXISTING);
				} else {
					Files.move(file, target.resolve(source.relativize(file)), REPLACE_EXISTING);
				}
				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				Path targetdir = target.resolve(source.relativize(dir));
				if (org) {
					Files.copy(dir, targetdir, REPLACE_EXISTING);
				} else {
					Files.move(dir, targetdir, REPLACE_EXISTING);
				}
				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return super.visitFileFailed(file, exc);
			}
		});
	}
}