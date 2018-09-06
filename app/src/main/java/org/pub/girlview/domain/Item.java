package org.pub.girlview.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
	private String title;
	private String src;
	private String href;

	public Item() {
	}

	public Item(String title, String src, String href) {
		this.title = title;
		this.src = src;
		this.href = href;
	}

	protected Item(Parcel in) {
		title = in.readString();
		src = in.readString();
		href = in.readString();
	}

	public static final Creator<Item> CREATOR = new Creator<Item>() {
		@Override
		public Item createFromParcel(Parcel in) {
			return new Item(in);
		}

		@Override
		public Item[] newArray(int size) {
			return new Item[size];
		}
	};

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(src);
		dest.writeString(href);
	}
}
