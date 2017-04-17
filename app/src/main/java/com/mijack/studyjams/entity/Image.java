package com.mijack.studyjams.entity;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

/**
 * @author Mr.Yuan
 * @date 2017/4/17
 */
public class Image extends Media{
    private long id;
    private long size;
    private String name;
    private long dateTaken;
    private String miniType;
    private int width;
    private int height;
    private double latitude;
    private double longitude;
    private int orientation;


    public Image(long id,String path, long size, String name, long dateTaken, String miniType,
                 int width, int height, double latitude, double longitude, int orientation) {
        super(path);
        this.id = id;
        this.size = size;
        this.name = name;
        this.dateTaken = dateTaken;
        this.miniType = miniType;
        this.width = width;
        this.height = height;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orientation = orientation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getMiniType() {
        return miniType;
    }

    public void setMiniType(String miniType) {
        this.miniType = miniType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }


    @NonNull
    public static Image getImageFromCursor(Cursor c) {
        return new Image(
                c.getLong(c.getColumnIndex(MediaStore.Images.Media._ID)),
                c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)),
                c.getLong(c.getColumnIndex(MediaStore.Images.Media.SIZE)),
                c.getString(c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)),
                c.getLong(c.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)),
                c.getString(c.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)),
                c.getInt(c.getColumnIndex(MediaStore.Images.Media.WIDTH)),
                c.getInt(c.getColumnIndex(MediaStore.Images.Media.HEIGHT)),
                c.getDouble(c.getColumnIndex(MediaStore.Images.Media.LATITUDE)),
                c.getDouble(c.getColumnIndex(MediaStore.Images.Media.LONGITUDE)),
                c.getInt(c.getColumnIndex(MediaStore.Images.Media.ORIENTATION)));
    }
}
