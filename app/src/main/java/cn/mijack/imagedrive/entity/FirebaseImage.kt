package cn.mijack.imagedrive.entity

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * @author admin
 * @date 2017/8/26
 */
class FirebaseImage(var miniType: String, var dateTaken: Long,
                    var downloadUrl: String, var fsUrl: String,
                    var deviceId: String, var width: Int, var height: Int,
                    var localPath: String, var name: String, var device: String, var uploadTime: Long) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong())

    constructor(image: Image, downloadUrl: String, fsUrl: String) :
            this(image.miniType!!, image.dateTaken, downloadUrl, fsUrl, Build.ID,
                    image.width, image.height, image.path, image.name!!,
                    Build.DEVICE, null!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(miniType)
        parcel.writeLong(dateTaken)
        parcel.writeString(downloadUrl)
        parcel.writeString(fsUrl)
        parcel.writeString(deviceId)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(localPath)
        parcel.writeString(name)
        parcel.writeString(device)
        parcel.writeLong(uploadTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toMap(): Map<String, Any> {
        uploadTime = System.currentTimeMillis()
        var map = HashMap<String, Any>()
        map.put("uploadTime", this.uploadTime)
        map.put("downloadUrl", this.downloadUrl)
        map.put("fsUrl", this.fsUrl)
        map.put("width", this.width)
        map.put("height", this.height)
        map.put("localPath", this.localPath)
        map.put("name", this.name)
        map.put("device", this.device)
        map.put("deviceId", this.deviceId)
        map.put("miniType", this.miniType)
        map.put("dateTaken", this.dateTaken)
        return map
    }

    companion object CREATOR : Parcelable.Creator<FirebaseImage> {
        override fun createFromParcel(parcel: Parcel): FirebaseImage {
            return FirebaseImage(parcel)
        }

        override fun newArray(size: Int): Array<FirebaseImage?> {
            return arrayOfNulls(size)
        }
    }
}