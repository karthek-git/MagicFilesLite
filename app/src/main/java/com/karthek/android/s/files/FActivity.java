package com.karthek.android.s.files;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.karthek.android.s.files.helper.SFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_SEND;
import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
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
		listFragment = (lsFrag) getFragmentManager().findFragmentById(R.id.frag_lsView);
	}

	int grant = 0;

	public SFile selectedFile;

	List<String> ClipBList;
	List<String> SelectedFileList;
	ListView lsView;
	public ImageButton addDirButton;
	SearchView searchView;
	View ToastView;
	TextView ToastTextView;

	public void m4() {
		lsView = listFragment.getListView();
		addDirButton = findViewById(R.id.rel_new_dir);
		lsView.setOnScrollListener(this);
		lsView.setMultiChoiceModeListener(this);
		toolbar = findViewById(R.id.t_fops);
		ToastView = getLayoutInflater().inflate(R.layout.solid_toast,
				findViewById(R.id.custom_toast_container));
		ToastTextView = ToastView.findViewById(R.id.text);
/*		ListView listView = findViewById(android.R.id.list);
		listView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
			@Override
			public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
				Log.v("windowins","called");
				v.setPadding(0,insets.getSystemWindowInsetTop(),0,
						addDirButton.getBackground().getIntrinsicHeight());
				//((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin =insets.getSystemWindowInsetTop();
				//((ViewGroup)v).getChildAt(0).dispatchApplyWindowInsets(insets);
				return insets;
			}
		});*/
	}


	@Override
	protected void onStart() {
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
		getActionBar().setHideOnContentScrollEnabled(true);
		searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
		searchView.setOnSearchClickListener(listFragment);
		searchView.setOnQueryTextListener(listFragment);
		searchView.setOnCloseListener(listFragment);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		System.out.println("gttt");
		int id = item.getItemId();
		if (id == R.id.ab_paste) {
			t_fops_paste();
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
		if (!searchView.isIconified()) {
			searchView.setIconified(true);
		} else if (!(listFragment.xBackPressed())) {
			super.onBackPressed();
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions,
										   int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			grant = 1;
			listFragment.getLoaderManager().restartLoader(0, null, listFragment);
			m4();
		} else {
			Toast.makeText(this, "GRANT PERMISSION TO ACCESS FILES", Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (searchView.isIconified() && !inActionMode) {
			if (scrollState == SCROLL_STATE_IDLE) {
				addDirButton.setVisibility(View.VISIBLE);
			} else {
				addDirButton.setVisibility(View.INVISIBLE);
			}
		}
		/*LinearLayout linearLayout = findViewById(R.id.coLayout);
		linearLayout.setPadding(0, getWindow().getDecorView().getRootWindowInsets().getSystemWindowInsetTop(), 0, 0);*/
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
	boolean inActionMode;
	Toolbar toolbar;


	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		inActionMode = true;
		addDirButton.setVisibility(View.GONE);
		actionMode = mode;
		SelectedFileList = new ArrayList<>();
		mode.setTitle("Selected");
		//getMenuInflater().inflate(R.menu.app_bar, menu);
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
		inActionMode = false;
		SelectedFileList = null;
		toolbar.setVisibility(View.GONE);
		addDirButton.setVisibility(View.VISIBLE);
		System.out.println("ac destroyed");
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return true;
	}


	public void m_fops(View view) {
		lsAdapter.ViewHolder viewHolder = (lsAdapter.ViewHolder) ((View) view.getParent()).getTag();
		selectedFile = viewHolder.sFile;
		Bundle bundle = new Bundle();
		bundle.putInt("FCase", 1);
		dirdialog = new dirDialog();
		dirdialog.setArguments(bundle);
		dirdialog.show(getFragmentManager(), "dia");
		/*sBottomDialog sBottomDialog = new sBottomDialog(this,R.style.BlackSanUI_Dialog);
		sBottomDialog.show();*/
	}

	int clp_org = 0;
	boolean Ccp;

	public void m_fops_yks(View view) {
		if (ClipBList != null) {
			ClipBList.clear();
		}
		if (view.getId() == R.id.t_fops_ynk) {
			clp_org = 1;
		} else {
			clp_org = 0;
		}
		if (selectedFile != null) {
			ClipBList = new ArrayList<>();
			ClipBList.add(selectedFile.file.getAbsolutePath());
			selectedFile = null;
		}
		Ccp = true;
		if (inActionMode) {
			ClipBList = SelectedFileList;
			actionMode.finish();
		} else {
			dirdialog.dismiss();
		}
		invalidateOptionsMenu();
	}

	public void m_fops_del(View view) {
		final List<String> DelList;
		if (inActionMode) {
			DelList = SelectedFileList;
			actionMode.finish();
		} else {
			dirdialog.dismiss();
			DelList = new ArrayList<>();
			DelList.add(selectedFile.file.getAbsolutePath());
			selectedFile = null;
		}

		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage(getResources().getString(R.string.del_progress));
		progressDialog.setMax(DelList.size());
		progressDialog.show();

		FApplication.executorService.execute(new Runnable() {
			@Override
			public void run() {
				for (String SFile : DelList) {
					try {
						if (new File(SFile).isDirectory()) {
							walkFileTree(Paths.get(SFile), new SimpleFileVisitor<Path>() {
								@Override
								public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
										throws IOException {
									Files.delete(file);
									inc();
									return FileVisitResult.CONTINUE;
								}

								@Override
								public FileVisitResult postVisitDirectory(Path dir, IOException e)
										throws IOException {
									if (e == null) {
										Files.delete(dir);
										inc();
										return FileVisitResult.CONTINUE;
									} else {
										throw e;
									}
								}
							});
						} else {
							Files.deleteIfExists(Paths.get(SFile));
							inc();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				progressDialog.dismiss();
				DelList.clear();
				listFragment.getLoaderManager().restartLoader(0, null, listFragment);
			}

			private void inc() {
				runOnUiThread(() -> progressDialog.incrementProgressBy(1));
			}
		});

	}


	public void m_fops_share(View view) {
		dirdialog.dismiss();
		Intent intent = new Intent(ACTION_SEND);
		String f = selectedFile.file.getAbsolutePath();
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://com.karthek.android.s.files.FProvider.file/" + Uri.encode(f)));
		intent.setType(selectedFile.getMimeType());
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

	int renm;

	public void m_fops_mkdir() {
		EditText editText = dirdialog.getDialog().findViewById(R.id.editDir);
		String renameTo = editText.getText().toString();
		if (renm == 1) {
			renm = 0;
			File file = selectedFile.file;
			File dest = new File(file.getParent(), renameTo);

			if (!(file.renameTo(dest))) {
				ToastTextView.setText(R.string.rename_err);
			} else {
				ToastTextView.setText(getResources().getString(R.string.renamed_holder, file.getName(),
						renameTo));
			}
			Toast toast = new Toast(this);
			toast.setView(ToastView);
			toast.show();
		} else {
			try {
				Files.createDirectory(Paths.get(listFragment.Cwd.getAbsolutePath(),
						String.valueOf(editText.getText())));
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
		bundle.putString("renamefile", selectedFile.file.getName());
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
		dirdialog.dismiss();
		new statDialog().show(getFragmentManager(), "info");
	}

	public void t_fops_paste() {
		final String CPath = listFragment.Cwd.getAbsolutePath();
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage(getResources().getString(R.string.progress_paste));
		progressDialog.setMax(ClipBList.size());
		progressDialog.show();
		FApplication.executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					paste();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private void paste() throws IOException {
				if (clp_org == 1) {
					for (String FileName : ClipBList) {
						Path path = Paths.get(FileName);
						if (new File(FileName).isDirectory()) {
							t_fops_paste_dir(path, Paths.get(CPath, path.getFileName().toString()), true);
						} else {
							FileUtils.copy(new FileInputStream(FileName),
									new FileOutputStream(CPath + "/" + path.getFileName()));
							//Files.copy(path, Paths.get(CPath, FileName), REPLACE_EXISTING);
						}
						inc();
					}
				} else {
					for (String SFile : ClipBList) {
						Path path = Paths.get(SFile);
						if (new File(SFile).isDirectory()) {
							t_fops_paste_dir(path, Paths.get(CPath, path.getFileName().toString()), false);
						} else {
							Files.move(path, Paths.get(CPath, path.getFileName().toString()),
									REPLACE_EXISTING);
						}
						inc();
					}
				}
				Ccp = false;
				invalidateOptionsMenu();
				ClipBList.clear();
				progressDialog.dismiss();
				listFragment.getLoaderManager().restartLoader(0, null, listFragment);
			}

			private void inc() {
				runOnUiThread(() -> progressDialog.incrementProgressBy(1));
			}
		});

		//m0(1);

	}

	private void t_fops_paste_dir(final Path source, final Path target, final boolean org) throws IOException {
		walkFileTree(source, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (org) {
					FileUtils.copy(new FileInputStream(file.toFile()),
							new FileOutputStream(target.resolve(source.relativize(file)).toFile()));
					//Files.copy(file, target.resolve(source.relativize(file)), REPLACE_EXISTING);
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
					return FileVisitResult.SKIP_SIBLINGS;
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