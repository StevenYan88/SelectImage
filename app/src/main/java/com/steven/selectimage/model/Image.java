package com.steven.selectimage.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description:
 * Data：9/4/2018-11:50 AM
 *
 * @author yanzhiwen
 */
public class Image implements Parcelable {

    private int id;
    private String path;
    private String thumbPath;
    private boolean isSelect;
    private String folderName;
    private String name;
    private long date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Image) {
            return this.path.equals(((Image) o).getPath());
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.path);
        dest.writeString(this.thumbPath);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeString(this.folderName);
        dest.writeString(this.name);
        dest.writeLong(this.date);
    }

    public Image() {
    }

    protected Image(Parcel in) {
        this.id = in.readInt();
        this.path = in.readString();
        this.thumbPath = in.readString();
        this.isSelect = in.readByte() != 0;
        this.folderName = in.readString();
        this.name = in.readString();
        this.date = in.readLong();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
