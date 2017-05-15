package com.serenegiant.usbcameracommon;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/3/8.
 */

public class OpenVideoTag implements Parcelable {
    public String filePath;
    boolean isOpenAudio;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isOpenAudio() {
        return isOpenAudio;
    }

    public void setOpenAudio(boolean openAudio) {
        isOpenAudio = openAudio;
    }

    public OpenVideoTag(String filePath, boolean isOpenAudio) {
        this.filePath = filePath;
        this.isOpenAudio = isOpenAudio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeByte(this.isOpenAudio ? (byte) 1 : (byte) 0);
    }

    public OpenVideoTag() {
    }

    protected OpenVideoTag(Parcel in) {
        this.filePath = in.readString();
        this.isOpenAudio = in.readByte() != 0;
    }

    public static final Parcelable.Creator<OpenVideoTag> CREATOR = new Parcelable.Creator<OpenVideoTag>() {
        @Override
        public OpenVideoTag createFromParcel(Parcel source) {
            return new OpenVideoTag(source);
        }

        @Override
        public OpenVideoTag[] newArray(int size) {
            return new OpenVideoTag[size];
        }
    };
}
