package com.karthek.android.s.files.helper;

import java.util.Comparator;

public class FComparator implements Comparator<SFile> {


	int SType;
	boolean asc;

	public FComparator(int SType, boolean asc) {
		this.SType = SType;
		this.asc = asc;
	}

	@Override
	public int compare(SFile o1, SFile o2) {
		boolean b1 = o1.isDir;
		boolean b2 = o2.isDir;
		if (b1 && !b2) {
			return -1;
		} else if (!b1 && b2) {
			return 1;
		} else {

			switch (SType) {
				case 0:
					return asc ? o1.file.compareTo(o2.file) : o2.file.compareTo(o1.file);
				case 1:
					return asc ? Long.compare(o1.size, o2.size) : Long.compare(o2.size, o1.size);
				case 2:
					return asc ? Long.compare(o1.modified, o2.modified) : Long.compare(o2.modified, o1.modified);
			}

		}
		return 0;
	}
}
