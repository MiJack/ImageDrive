package cn.mijack.imagedrive.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.Yuan
 * @date 2017/4/17
 */
public class Utils {
    public static int size(Collection collection) {
        return collection == null ? 0 : collection.size();
    }

    public static void close(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable c : closeables) {
                if (c != null) {
                    try {
                        c.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static String fileMD5(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    public static String fileExtensionName(File file) {
        String path = file.getPath();
        int lastIndexOf = path.lastIndexOf(".");
        if (lastIndexOf > 0) {
            return path.substring(lastIndexOf);
        }
        return null;
    }

    public static String base64Encode(String string) {
        return new String(Base64.encode(string.getBytes(), Base64.NO_WRAP));
    }

    public static String base64Decode(String string) {
        return new String(Base64.decode(string.getBytes(), Base64.NO_WRAP));
    }

    public static boolean isEmpty(Uri uri) {
        return uri == null || TextUtils.isEmpty(uri.toString());
    }

    public static String formatTime(long time) {
        return DateFormat.getDateInstance().format(new Date(time));
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_ALL);
        return list.size() > 0;
    }
}
