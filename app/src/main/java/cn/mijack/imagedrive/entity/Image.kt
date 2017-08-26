package cn.mijack.imagedrive.entity

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore

/**
 * @author admin
 * @date 2017/8/26
 */

class Image(var id: Long, path: String, var size: Long, var name: String?, var dateTaken: Long, var miniType: String?,
            var width: Int, var height: Int, var latitude: Double, var longitude: Double, var orientation: Int) : Media(path), Parcelable {

    constructor(parcel: Parcel) : this(parcel.readLong(), parcel.readString(), parcel.readLong(), parcel.readString(), parcel.readLong(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readDouble(), parcel.readDouble(), parcel.readInt()) {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(path)
        dest.writeLong(size)
        dest.writeString(name)
        dest.writeLong(dateTaken)
        dest.writeString(miniType)
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeDouble(latitude)
        dest.writeDouble(longitude)
        dest.writeInt(orientation)
    }

    override fun toString(): String {
        return "Image{" +
                "id=" + id +
                ", size=" + size +
                ", name='" + name + '\'' +
                ", dateTaken=" + dateTaken +
                ", miniType='" + miniType + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", orientation=" + orientation +
                '}'
    }

    companion object {


        fun getImageFromCursor(c: Cursor): Image {
            return Image(
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
                    c.getInt(c.getColumnIndex(MediaStore.Images.Media.ORIENTATION)))
        }

        val CREATOR: Parcelable.Creator<Image> = object : Parcelable.Creator<Image> {
            override fun createFromParcel(parcel: Parcel): Image {
                return Image(parcel)
            }

            override fun newArray(size: Int): Array<Image?> {
                return arrayOfNulls(size)
            }
        }
    }
}
