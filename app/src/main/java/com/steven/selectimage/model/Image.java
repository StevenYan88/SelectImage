package com.steven.selectimage.model;

/**
 * Description:
 * Data：9/4/2018-11:50 AM
 *
 * @author yanzhiwen
 */
public class Image {
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
}
