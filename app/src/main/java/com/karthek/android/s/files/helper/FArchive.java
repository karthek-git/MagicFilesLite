package com.karthek.android.s.files.helper;

import java.nio.file.Paths;

public class FArchive {
    private static native synchronized int c_archive_extract(int i, String str);

    private static native synchronized long c_archive_list(int i);

    public static void listArchiveEntries(String FileName) {
        c_archive_list(SFile.getNativeFD(FileName));
    }

    public static void extractArchive(String filename) {
        c_archive_extract(SFile.getNativeFD(filename), Paths.get(filename, new String[0]).getParent().toString());
    }
}
