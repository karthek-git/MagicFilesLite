package com.karthek.android.s.files;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.karthek.android.s.files.helper.FilePreview;
import com.karthek.android.s.files.helper.SFile;

import java.io.File;

public class lsAdapter extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener {
	SFile[] dens;
	lsFrag pFragment;
	ListView listView;

	public lsAdapter(lsFrag frag) {
		pFragment = frag;
		listView = frag.getListView();
		dens = frag.sFiles;
	}

	@Override
	public int getCount() {
		if (dens != null) {
			return dens.length;
		}
		return 0;
	}

	@Override
	public File getItem(int position) {
		return dens[position].file;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			System.out.println("getview:" + position);

			viewHolder = new ViewHolder();
			viewHolder.context = parent.getContext();
			convertView = LayoutInflater.from(viewHolder.context).inflate(R.layout.lslayout, parent, false);
			convertView.setTag(viewHolder);
			viewHolder.imageView = convertView.findViewById(R.id.img);
			viewHolder.FileName = convertView.findViewById(R.id.file_name);
			viewHolder.FileSize = convertView.findViewById(R.id.tv_fsize);

			viewHolder.imageView.setOnClickListener(this);
			//viewHolder.FileName.setOnLongClickListener(this);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.sFile = dens[position];
		viewHolder.pos = position;
		viewHolder.FileName.setText(dens[position].file.getName());

		if (dens[position].isDir) {
			viewHolder.imageView.setImageResource(R.drawable.ic_dir_prev);
			int size = (int) dens[position].size;
			if (size == -1) {
				FApplication.executorService.execute(new lsDir(viewHolder.sFile, viewHolder, position));
			} else {
				viewHolder.FileSize.setText(viewHolder.context.getResources().getQuantityString(R.plurals.items, size, size));
			}
		} else {
			viewHolder.FileSize.setText(Formatter.formatFileSize(viewHolder.context, dens[position].size));
			Bitmap bitmap = pFragment.getBitmapFromMemCache(dens[position].file.getAbsolutePath());
			if (bitmap != null) {
				viewHolder.imageView.setImageBitmap(bitmap);
			} else {
				viewHolder.imageView.setImageResource(dens[position].res);
				FApplication.executorService.execute(new FilePreview(viewHolder.context, pFragment, viewHolder, position, dens[position]));
			}
		}
		//convertView.setFocusable(true);
		return convertView;
	}

	public static class ViewHolder {
		Context context;
		SFile sFile;
		public ImageView imageView;
		TextView FileName;
		TextView FileSize;
		public int pos;
	}


	@Override
	public void onClick(View v) {
		int pos = listView.getPositionForView(v);
		listView.setItemChecked(pos, !listView.isItemChecked(pos));
	}

	@Override
	public boolean onLongClick(View v) {
		System.out.println("onlongc");
		//((TextView) v).setTextColor(Color.CYAN);
		/*FActivity.selectedFile = (String) ((TextView) v).getText();
		RelativeLayout relativeLayout = (RelativeLayout) v.getParent().getParent().getParent().getParent();
		relativeLayout.getChildAt(1).setVisibility(View.VISIBLE);*/
		/*ListView listView = (ListView) v.getParent().getParent();
		int pos = listView.getPositionForView(v);
		listView.setItemChecked(pos, !listView.isItemChecked(pos));*/

		v.getParent().getParent().showContextMenuForChild(v);
		return false;
	}

	@Override
	public void notifyDataSetChanged() {
		dens = pFragment.sFiles;
		//super.notifyDataSetChanged();
	}

	private static class lsDir implements Runnable {

		SFile dir;
		ViewHolder viewHolder;
		int position;

		public lsDir(SFile dir, ViewHolder viewHolder, int position) {
			this.dir = dir;
			this.viewHolder = viewHolder;
			this.position = position;
		}

		@Override
		public void run() {
			dir.initDir();
			if (viewHolder.pos == position) {
				viewHolder.FileSize.post(() -> {
					int size = (int) dir.size;
					viewHolder.FileSize.setText(viewHolder.context.getResources().getQuantityString(R.plurals.items,
							size,
							size));
				});
			}
		}
	}

}
