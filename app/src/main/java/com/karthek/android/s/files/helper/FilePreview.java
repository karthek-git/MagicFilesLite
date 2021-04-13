package com.karthek.android.s.files.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.media.ThumbnailUtils;
import android.os.ParcelFileDescriptor;
import android.util.Size;
import android.widget.ImageView;

import com.karthek.android.s.files.R;
import com.karthek.android.s.files.lsAdapter;
import com.karthek.android.s.files.lsFrag;

import java.io.File;
import java.io.IOException;

public class FilePreview implements Runnable {

	SFile sFile;
	String filename;
	ImageView imageView;
	Context context;
	lsFrag frag;
	lsAdapter.ViewHolder viewHolder;
	int pos;

	public FilePreview(Context context, lsFrag pFragment, lsAdapter.ViewHolder viewHolder, int position, SFile sFile) {
		this.sFile = sFile;
		frag = pFragment;
		this.imageView = viewHolder.imageView;
		this.context = context;
		this.viewHolder = viewHolder;
		pos = position;
	}

	@Override
	public void run() {
		filename = sFile.file.getAbsolutePath();
		setFilePreviewIcon();
	}

	public void setFilePreviewIcon() {
		Size size = new Size(100, 100);
		Bitmap bitmap = null;
		String FType = FileType.getFileMIMEType(filename);
		if (FType.startsWith("image/")) {
			sFile.res = R.drawable.ic_img_prev;
			try {
				bitmap = ThumbnailUtils.createImageThumbnail(new File(filename), size, null);
			} catch (IOException e) {
				setonUIThread(sFile.res);
			}
			if (bitmap != null)
				setonUIThread(bitmap);
		} else if (FType.startsWith("audio/")) {
			sFile.res = R.drawable.ic_aud_prev;
			try {
				bitmap = ThumbnailUtils.createAudioThumbnail(new File(filename), size, null);
			} catch (IOException e) {
				setonUIThread(sFile.res);
			}
			if (bitmap != null)
				setonUIThread(bitmap);
		} else if (FType.startsWith("video/")) {
			sFile.res = R.drawable.ic_vid_prev;
			try {
				bitmap = ThumbnailUtils.createVideoThumbnail(new File(filename), size, null);
			} catch (IOException e) {
				setonUIThread(sFile.res);
			}
			if (bitmap != null)
				setonUIThread(bitmap);
		} else if (FType.equals("application/vnd.android.package-archive")) {
			PackageManager packageManager = context.getPackageManager();
			setonUIThread(packageManager.getApplicationIcon(packageManager.getPackageArchiveInfo(filename, 0).applicationInfo));
		} else if (FType.equals("application/pdf")) {
			sFile.res = R.drawable.ic_pdf_prev;
			try {
				PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(new File(filename), ParcelFileDescriptor.MODE_READ_ONLY));
				PdfRenderer.Page page = renderer.openPage(0);
				bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
				bitmap.eraseColor(Color.WHITE);
				page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
				page.close();
				renderer.close();
			} catch (IOException | SecurityException e) {
				setonUIThread(sFile.res);
			}
			setonUIThread(bitmap);
		} else {
			setonUIThread(getFileSIcon(FType));
		}
	}

	private void setonUIThread(final int res) {
		imageView.post(new Runnable() {
			@Override
			public void run() {
				imageView.setImageResource(res);
			}
		});
	}

	private void setonUIThread(final Object icon) {
		imageView.post(new Runnable() {
			public void run() {
				if (icon != null) {
					if (icon instanceof Bitmap) {
						if (viewHolder.pos == pos) {
							imageView.setImageBitmap((Bitmap) icon);
						}
						frag.addBitmapToMemoryCache(filename, (Bitmap) icon);
					} else {
						imageView.setImageDrawable((Drawable) icon);
					}
				}
			}
		});
	}

	public static int getFileSIcon(String mimeType) {
		switch (mimeType) {
			case "vnd.android.document/directory":
				return R.drawable.ic_dir_prev;
			case "text/x-vcard":
			case "text/vcard":
				return R.drawable.ic_vcf_prev;
			case "text/calendar":
			case "text/x-vcalendar":
				return R.drawable.ic_cal_prev;
			case "application/x-quicktimeplayer":
			case "application/x-shockwave-flash":
				return R.drawable.ic_vid_prev;
			case "application/x-debian-package":
			case "application/vnd.debian.binary-package":
			case "application/x-deb":
				return R.drawable.ic_deb_prev;

			case "application/x-bzip2":
			case "application/x-lz4":
			case "application/x-xz":
			case "application/mac-binhex40":
			case "application/rar":
			case "application/zip":
			case "application/x-apple-diskimage":
			case "application/x-gtar":
			case "application/x-iso9660-image":
			case "application/x-lha":
			case "application/x-lzh":
			case "application/x-lzx":
			case "application/x-stuffit":
			case "application/x-tar":
			case "application/x-webarchive":
			case "application/x-webarchive-xml":
			case "application/gzip":
			case "application/x-7z-compressed":
			case "application/x-rar-compressed":
			case "application/java-archive":
				return R.drawable.ic_archive_prev;
			case "application/x-pem-file":
			case "application/pgp-keys":
			case "application/pgp-signature":
			case "application/x-pkcs12":
			case "application/x-pkcs7-certreqresp":
			case "application/x-pkcs7-crl":
			case "application/x-x509-ca-cert":
			case "application/x-x509-user-cert":
			case "application/x-pkcs7-certificates":
			case "application/x-pkcs7-mime":
			case "application/x-pkcs7-signature":
				return R.drawable.ic_certs_prev;
			case "application/rdf+xml":
			case "application/rss+xml":
			case "application/x-object":
			case "application/xhtml+xml":
			case "text/css":
			case "text/html":
			case "text/xml":
			case "text/x-c++hdr":
			case "text/x-c++src":
			case "text/x-chdr":
			case "text/x-csrc":
			case "text/x-dsrc":
			case "text/x-csh":
			case "text/x-haskell":
			case "text/x-java":
			case "text/x-literate-haskell":
			case "text/x-pascal":
			case "text/x-tcl":
			case "text/x-tex":
			case "application/x-latex":
			case "application/x-texinfo":
			case "application/atom+xml":
			case "application/ecmascript":
			case "application/json":
			case "application/javascript":
			case "application/xml":
			case "text/javascript":
			case "application/x-javascript":
				return R.drawable.ic_codes_prev;
			case "application/x-font":
			case "application/font-woff":
			case "application/x-font-woff":
			case "application/x-font-ttf":
			case "application/vnd.oasis.opendocument.graphics":
			case "application/vnd.oasis.opendocument.graphics-template":
			case "application/vnd.oasis.opendocument.image":
			case "application/vnd.stardivision.draw":
			case "application/vnd.sun.xml.draw":
			case "application/vnd.sun.xml.draw.template":
			case "application/vnd.google-apps.drawing":


			case "application/vnd.stardivision.impress":
			case "application/vnd.sun.xml.impress":
			case "application/vnd.sun.xml.impress.template":
			case "application/x-kpresenter":
			case "application/vnd.oasis.opendocument.presentation":
			case "application/vnd.google-apps.presentation":

			case "application/vnd.oasis.opendocument.spreadsheet":
			case "application/vnd.oasis.opendocument.spreadsheet-template":
			case "application/vnd.stardivision.calc":
			case "application/vnd.sun.xml.calc":
			case "application/vnd.sun.xml.calc.template":
			case "application/x-kspread":
			case "application/vnd.google-apps.spreadsheet":

			case "application/vnd.oasis.opendocument.text":
			case "application/vnd.oasis.opendocument.text-master":
			case "application/vnd.oasis.opendocument.text-template":
			case "application/vnd.oasis.opendocument.text-web":
			case "application/vnd.stardivision.writer":
			case "application/vnd.stardivision.writer-global":
			case "application/vnd.sun.xml.writer":
			case "application/vnd.sun.xml.writer.global":
			case "application/vnd.sun.xml.writer.template":
			case "application/x-abiword":
			case "application/x-kword":
			case "application/vnd.google-apps.document":


			case "application/msword":
			case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
			case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":

			case "application/vnd.ms-excel":
			case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
			case "application/vnd.openxmlformats-officedocument.spreadsheetml.template":

			case "application/vnd.ms-powerpoint":
			case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
			case "application/vnd.openxmlformats-officedocument.presentationml.template":
			case "application/vnd.openxmlformats-officedocument.presentationml.slideshow":
			default:
				if (mimeType.startsWith("text/"))
					return R.drawable.ic_txt_prev;
				return R.drawable.ic_file_prev;
		}
	}


}
