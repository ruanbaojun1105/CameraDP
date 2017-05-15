package com.hwx.camera_doul.cameradp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/3/10.
 */

public class Item implements Parcelable {
    String path;
    String name;

    public Item(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
    }

    public Item() {
    }

    protected Item(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
