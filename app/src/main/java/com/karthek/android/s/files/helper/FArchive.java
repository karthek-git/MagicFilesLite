package com.karthek.android.s.files.helper;

import java.nio.file.Paths;

public class FArchive {

	public FArchive() {
		System.loadLibrary("archive-wrapper");
	}

	private native synchronized int c_archive_extract(int i, String target);

	private native synchronized long c_archive_list(int i);

	public void listArchiveEntries(String FileName) {
		c_archive_list(SFile.getNativeFD(FileName));
	}

	public void extractArchive(String filename) {
		c_archive_extract(SFile.getNativeFD(filename), Paths.get(filename, new String[0]).getParent().toString());
	}

	public int extractArchive(int fd, String target) {
		return c_archive_extract(fd, target);
	}
}
