package com.karthek.android.s.files.helper;

import java.util.Comparator;

public class FComparator implements Comparator<SFile> {


	int SType;

	public FComparator(int SType) {
		this.SType = SType;
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
					return o1.file.compareTo(o2.file);
				case 1:
					return Long.compare(o1.size, o2.size);
				case 2:
					return Long.compare(o1.modified, o2.modified);
			}

		}
		return 0;
	}
}
