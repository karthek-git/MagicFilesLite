package com.karthek.android.s.files.helper;

import java.text.Collator;
import java.util.Comparator;

public class FComparator implements Comparator<SFile> {


	private final int SType;
	private final boolean asc;
	private final Collator collator = Collator.getInstance();

	public FComparator(int SType, boolean asc) {
		this.SType = SType;
		this.asc = asc;
	}

	@Override
	public int compare(SFile o1, SFile o2) {
		final boolean b1 = o1.isDir;
		final boolean b2 = o2.isDir;
		if (b1 && !b2) {
			return -1;
		} else if (!b1 && b2) {
			return 1;
		} else {
			switch (SType) {
				case 0:
					return asc ? collator.compare(o1.file.getName(), o2.file.getName()) :
							collator.compare(o2.file.getName(), o1.file.getName());
				case 1:
					return asc ? Long.compare(o1.size, o2.size) : Long.compare(o2.size, o1.size);
				case 2:
					return asc ? Long.compare(o1.modified, o2.modified) : Long.compare(o2.modified, o1.modified);
			}

		}
		return 0;
	}
}
