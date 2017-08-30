package cn.mijack.imagedrive.core

import android.content.Context
import android.provider.MediaStore
import android.support.media.ExifInterface
import cn.mijack.imagedrive.entity.Folder
import cn.mijack.imagedrive.entity.Image
import cn.mijack.imagedrive.entity.Media
import cn.mijack.imagedrive.util.Utils
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

/**
 * @author admin
 * @date 2017/8/30
 */
class MediaManager {

    companion object {
        fun getImageFolderWithImages(context: Context): List<Folder> {
            return getImagesFromFolder(context, -1)
        }

        fun getImagesFromFolder(context: Context, limit: Int): List<Folder> {
            var contentResolver = context.contentResolver
            var result = ArrayList<Folder>()
            //table images
            //table files
            var contentUri = MediaStore.Files.getContentUri("external")
            var folderCursor = contentResolver.query(contentUri
                    , Array<String>(3) { MediaStore.Images.ImageColumns.DATA; "count(*)"; MediaStore.Files.FileColumns.PARENT },
                    MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                            + ") group by " +
                            MediaStore.Files.FileColumns.PARENT + " --"
                    , null, null)
            if (folderCursor != null && folderCursor.moveToFirst()) {
                do {
                    System.out.println("-----------------------------------------------------")
                    var file = File(folderCursor.getString(0))
                    var count = folderCursor.getInt(1)
                    var parentId = folderCursor.getInt(2)
                    var images = getImagesFromFolder(context, file.getParent(), limit)
                    var imageMediaFolder = Folder(file.getParent())
                    imageMediaFolder.data = images//(images)
                    imageMediaFolder.count = count
                    result.add(imageMediaFolder)
                } while (folderCursor.moveToNext())
            }

            Utils.close(folderCursor)
            return result
        }

        fun getImagesFromFolder(context: Context, folder: String, limit: Int): List<Image> {
            var images = ArrayList<Image>()
            var contentResolver = context.contentResolver
            var contentUri = MediaStore.Files.getContentUri("external")
            var select = MediaStore.Images.Media.DATA +
                    " like '" + folder + "%' and " +
                    MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + ") " +
                    (if (limit > 0) ("limit " + limit) else "") + " --"
            var c = contentResolver.query(contentUri, Array(11) {
                MediaStore.Images.Media._ID; MediaStore.Images.Media.DATA; MediaStore.Images.Media.SIZE;
                MediaStore.Images.Media.DISPLAY_NAME; MediaStore.Images.Media.DATE_TAKEN; MediaStore.Images.Media.MIME_TYPE;
                MediaStore.Images.Media.WIDTH; MediaStore.Images.Media.HEIGHT; MediaStore.Images.Media.LATITUDE;
                MediaStore.Images.Media.LONGITUDE; MediaStore.Images.Media.ORIENTATION
            }, select, null, null)
            if (c != null && c.moveToFirst()) {
                do {
                    System.out.println(
                            c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)));
                    var image = Image.getImageFromCursor(c)
                    images.add(image)
                } while (c.moveToNext())
            }
            Utils.Companion.close(c)
            return images
        }

        fun flatFolder(folders: List<Folder>): List<Media> {
            var result = ArrayList<Media>()
            for (f: Folder in folders) {
                result.add(f)
                result.addAll(f.data)
            }
            return result
        }

        fun getImagesInFolder(context: Context, folderPath: String): List<Image> {
            return getImagesFromFolder(context, folderPath, -1)
        }

        fun getImages(context: Context): List<Image> {
            var images = ArrayList<Image>();
            var contentUri = MediaStore.Files.getContentUri("external");
            var select = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
            var c = context.contentResolver.query(contentUri, Array<String>(11) {
                MediaStore.Images.Media._ID;
                MediaStore.Images.Media.DATA;
                MediaStore.Images.Media.SIZE;
                MediaStore.Images.Media.DISPLAY_NAME;
                MediaStore.Images.Media.DATE_TAKEN;
                MediaStore.Images.Media.MIME_TYPE;
                MediaStore.Images.Media.WIDTH;
                MediaStore.Images.Media.HEIGHT;
                MediaStore.Images.Media.LATITUDE;
                MediaStore.Images.Media.LONGITUDE;
                MediaStore.Images.Media.ORIENTATION
            }, select, null, null)
            if (c != null && c.moveToFirst()) {
                do {
                    System.out.println(
                            c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)))
                    var image = Image.getImageFromCursor(c)
                    images.add(image)
                } while (c.moveToNext())
            }
            Utils.close(c)
            return images;
        }

        //
        fun getExifInterface(imagePath: String): ExifInterface? {
            try {
                var fis = FileInputStream(imagePath)
                var exifInterface = ExifInterface(fis)
//            exifInterface.
                return exifInterface
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        //
        fun deleteFile(path: String): Boolean {
            var file = File(path)
            return file.delete()
        }
    }
//
}

