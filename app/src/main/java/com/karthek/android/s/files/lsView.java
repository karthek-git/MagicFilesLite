package com.karthek.android.s.files;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class lsView extends LinearLayout implements Checkable {

	private boolean fChecked;

	private static final int[] CHECKED_STATE_SET = {
			android.R.attr.state_checked
	};

	public lsView(Context context) {
		super(context);
	}

	public lsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public lsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (fChecked) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	@Override
	public void setChecked(boolean checked) {
		System.out.println("setchecked " + checked);
		fChecked = checked;
		refreshDrawableState();
	}

	@Override
	public boolean isChecked() {
		return fChecked;
	}

	@Override
	public void toggle() {
		fChecked = !fChecked;
	}


	static class SavedState extends BaseSavedState {
		boolean checked;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			checked = (Boolean) in.readValue(getClass().getClassLoader());
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeValue(checked);
		}

		@Override
		public String toString() {
			return "CheckableImageView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + checked + "}";
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.checked = fChecked;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;

		super.onRestoreInstanceState(ss.getSuperState());
		setChecked(ss.checked);
		requestLayout();
	}
}
