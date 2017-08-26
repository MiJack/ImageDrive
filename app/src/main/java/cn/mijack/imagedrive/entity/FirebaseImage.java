package cn.mijack.imagedrive.entity;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.Yuan
 * @date 2017/5/14
 */
public class FirebaseImage implements Parcelable {
    private String miniType;
    private long dateTaken;
    private String downloadUrl;
    private String fsUrl;
    private String deviceId;
    private int width;
    private int height;
    private String localPath;
    private String name;
    private String device;
    private long uploadTime;

    public FirebaseImage() {
    }

    public FirebaseImage(Image image, String downloadUrl, String fsUrl) {
        this.downloadUrl = downloadUrl;
        this.fsUrl = fsUrl;
        this.dateTaken = image.getDateTaken();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.localPath = image.getPath();
        this.name = image.getName();
        this.miniType = image.getMiniType();
        this.device = Build.DEVICE;
        this.deviceId = Build.ID;
    }

    protected FirebaseImage(Parcel in) {
        miniType = in.readString();
        dateTaken = in.readLong();
        downloadUrl = in.readString();
        fsUrl = in.readString();
        deviceId = in.readString();
        width = in.readInt();
        height = in.readInt();
        localPath = in.readString();
        name = in.readString();
        device = in.readString();
        uploadTime = in.readLong();
    }

    public static final Creator<FirebaseImage> CREATOR = new Creator<FirebaseImage>() {
        @Override
        public FirebaseImage createFromParcel(Parcel in) {
            return new FirebaseImage(in);
        }

        @Override
        public FirebaseImage[] newArray(int size) {
            return new FirebaseImage[size];
        }
    };

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFsUrl() {
        return fsUrl;
    }

    public void setFsUrl(String fsUrl) {
        this.fsUrl = fsUrl;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getMiniType() {
        return miniType;
    }

    public void setMiniType(String miniType) {
        this.miniType = miniType;
    }

    public Map<String, Object> toMap() {
        uploadTime = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        map.put("uploadTime", this.uploadTime);
        map.put("downloadUrl", this.downloadUrl);
        map.put("fsUrl", this.fsUrl);
        map.put("width", this.width);
        map.put("height", this.height);
        map.put("localPath", this.localPath);
        map.put("name", this.name);
        map.put("device", this.device);
        map.put("deviceId", this.deviceId);
        map.put("miniType", this.miniType);
        map.put("dateTaken", this.dateTaken);
        return map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(miniType);
        dest.writeLong(dateTaken);
        dest.writeString(downloadUrl);
        dest.writeString(fsUrl);
        dest.writeString(deviceId);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(localPath);
        dest.writeString(name);
        dest.writeString(device);
        dest.writeLong(uploadTime);
    }
}
