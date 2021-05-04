package com.karthek.android.s.files.helper;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.nio.file.Paths;

import static com.karthek.android.s.files.helper.SFile.getNativeFD;
import static java.nio.file.Files.probeContentType;

public class FileType {

	long MagicCookie;

	public FileType(Context context) {
		System.loadLibrary("magic-wrapper");
		MagicCookie = c_magic_open(context.getAssets());
		System.out.println("mgc initttttttt");
	}

	private native synchronized long c_magic_open(AssetManager assetManager);

	private native synchronized String c_magic_descriptor(long magicCookie, int fd);

	private native synchronized void c_magic_setflags(long magicCookie, int Flag);

	private native synchronized String c_magic_error(long magicCookie);


	public String getFileMIMEType(String filename) {
		String string = null;
		try {
			string = probeContentType(Paths.get(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (string == null || string.contains("oct")) {
			string = getFileMIMETypeMGC(filename);
		}
		return string;
	}

	private String getFileMIMETypeMGC(String filename) {
		String mime;
		int fd = getNativeFD(filename);
		if (fd != -1) {
			mime = c_magic_descriptor(MagicCookie, fd);
			System.out.println("from file:" + mime);
			if (mime == null) {
				System.out.println(c_magic_error(MagicCookie));
				return "application/octet-stream";
			} else {
				return mime;
			}
		}
		return "application/octet-stream";
	}

	public String getFileMInfo(String filename) {
		c_magic_setflags(MagicCookie, 1);
		String string = c_magic_descriptor(MagicCookie, getNativeFD(filename));
		c_magic_setflags(MagicCookie, 0);
		System.out.println(string);
		return string;
	}

	/*public enum FileTypeCat {
		DIR(R.drawable.ic_dir_prev),
		FILE(R.drawable.ic_file_prev),

		IMAGE(R.drawable.ic_img_prev),
		AUDIO(R.drawable.ic_aud_prev),
		VIDEO(R.drawable.ic_vid_prev),

		CALENDAR(R.drawable.ic_cal_prev),
		CONTACT(R.drawable.ic_vcf_prev),
		TEXT(R.drawable.ic_txt_prev),
		CERTIFICATE(R.drawable.ic_certs_prev),

		APK(android.R.drawable.sym_def_app_icon),
		ARCHIVE(R.drawable.ic_archive_prev),
		DEB(R.drawable.ic_deb_prev),

		PDF(R.drawable.ic_pdf_prev),
		DOC(R.drawable.ic_file_prev),
		DRAW(R.drawable.ic_file_prev),
		CALC(R.drawable.ic_file_prev);

		int res;

		FileTypeCat(int res) {
			this.res = res;
		}
	}*/


}
