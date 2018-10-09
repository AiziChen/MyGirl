package org.pub.girlview.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 游客信息
 */
public class Gesture implements Parcelable {

    private String name;
    private String comment;
    private String area;
    private String time;

    public Gesture() {
    }

    public Gesture(String name, String comment, String area, String time) {
        super();
        this.name = name;
        this.comment = comment;
        this.area = area;
        this.time = time;
    }

    protected Gesture(Parcel in) {
        name = in.readString();
        comment = in.readString();
        area = in.readString();
        time = in.readString();
    }

    public static final Creator<Gesture> CREATOR = new Creator<Gesture>() {
        @Override
        public Gesture createFromParcel(Parcel in) {
            return new Gesture(in);
        }

        @Override
        public Gesture[] newArray(int size) {
            return new Gesture[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(comment);
        dest.writeString(area);
        dest.writeString(time);
    }
}

