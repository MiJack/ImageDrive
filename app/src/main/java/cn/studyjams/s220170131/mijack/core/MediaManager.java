package cn.studyjams.s220170131.mijack.core;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import cn.studyjams.s220170131.mijack.entity.Folder;
import cn.studyjams.s220170131.mijack.entity.Image;
import cn.studyjams.s220170131.mijack.entity.Media;
import cn.studyjams.s220170131.mijack.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Yuan
 * @date 2017/4/17
 */
public class MediaManager {
    public static final String TAG = "MediaManager";

    public static List<Folder> getImageFolderWithImages(Context context) {
        return getImageFolderWithImages(context, -1);
    }

    public static List<Folder> getImageFolderWithImages(Context context, int limit) {
        ContentResolver contentResolver = context.getContentResolver();
        List<Folder> result = new ArrayList<>();
        //table images
        //table files
        Uri contentUri = MediaStore.Files.getContentUri("external");
        Cursor folderCursor = contentResolver.query(contentUri
                , new String[]{MediaStore.Images.ImageColumns.DATA, "count(*)", MediaStore.Files.FileColumns.PARENT},
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + ") group by " +
                        MediaStore.Files.FileColumns.PARENT + " --"
                , null, null);
        if (folderCursor != null && folderCursor.moveToFirst()) {
            do {
                System.out.println("-----------------------------------------------------");
                File file = new File(folderCursor.getString(0));
                int count = folderCursor.getInt(1);
                int parentId = folderCursor.getInt(2);
                List<Image> images = getImagesFromFolder(context, file.getParent(), limit);
                Log.d(TAG, "getImageFoldersFromMedia: size:" + Utils.size(images));
                Log.d(TAG, "getImageFoldersFromMedia: count:" + count);
                Log.d(TAG, "getImageFoldersFromMedia: parent:" + parentId);
                Folder imageMediaFolder = new Folder(file.getParent());
                imageMediaFolder.setData(images);
                imageMediaFolder.setCount(count);
                result.add(imageMediaFolder);
            } while (folderCursor.moveToNext());
        }

        Utils.close(folderCursor);
        return result;
    }

    private static List<Image> getImagesFromFolder(Context context, String folder, int limit) {
        List<Image> images = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = MediaStore.Files.getContentUri("external");
        String select = MediaStore.Images.Media.DATA + " like '" + folder + "%' and "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + ") limit " + limit + /*" order by" + MediaStore.Files.FileColumns.DATE_MODIFIED +*/ " --";
        System.out.println(select);
        Cursor c = contentResolver.query(contentUri, new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.ORIENTATION
        }, select, null, null);
        if (c != null && c.moveToFirst()) {
            Log.d(TAG, "getImagesFromFolder: cursor count:" + c.getCount());
            do {
                System.out.println(
                        c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)));
                Image image = Image.getImageFromCursor(c);
                images.add(image);
            } while (c.moveToNext());
        }
        Utils.close(c);
        return images;
    }


    public static List<Media> flatFolder(List<Folder> folders) {
        List<Media> result = new ArrayList<>();
        for (Folder folder : folders) {
            result.add(folder);
            result.addAll(folder.getData());
        }
        return result;
    }

    public static List<Image> getImages(Context context) {
        List<Image> images = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = MediaStore.Files.getContentUri("external");
        String select = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        System.out.println(select);
        Cursor c = contentResolver.query(contentUri, new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.ORIENTATION
        }, select, null, null);
        if (c != null && c.moveToFirst()) {
            Log.d(TAG, "getImages: cursor count:" + c.getCount());
            do {
                System.out.println(
                        c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA)));
                Image image = Image.getImageFromCursor(c);
                images.add(image);
            } while (c.moveToNext());
        }
        Utils.close(c);
        return images;
    }
}
