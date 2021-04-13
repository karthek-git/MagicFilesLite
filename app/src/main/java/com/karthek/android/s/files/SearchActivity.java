package com.karthek.android.s.files;

import android.app.Activity;
import android.app.ListFragment;
import android.app.SearchManager;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import static java.nio.file.Files.walkFileTree;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String srq = getIntent().getStringExtra(SearchManager.QUERY);
				lsAdapter lsAdapter = null;
				lsAdapter = new lsAdapter(null);
				final com.karthek.android.s.files.lsAdapter finalLsAdapter = lsAdapter;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ListFragment listFragment = (ListFragment) getFragmentManager().findFragmentById(R.id.frag_sl);
						listFragment.setListAdapter(finalLsAdapter);
						System.out.println("runned");
					}
				});
			}
		}).start();
	}

	private File[] getSearchFiles(String query) throws IOException {
		final ArrayList<File> fileArrayList = new ArrayList<>();
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**" + query + "**");
		walkFileTree(Paths.get("/storage/emulated/0"), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (pathMatcher.matches(dir)) {
					System.out.println(dir);
					fileArrayList.add(dir.toFile());
				}
				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (pathMatcher.matches(file)) {
					System.out.println(file);
					fileArrayList.add(file.toFile());
				}
				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) {
				return FileVisitResult.CONTINUE;
			}
		});

		return fileArrayList.toArray(new File[0]);
	}

}