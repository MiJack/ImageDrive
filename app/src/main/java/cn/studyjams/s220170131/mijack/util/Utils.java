package cn.studyjams.s220170131.mijack.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Base64;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import cn.studyjams.s220170131.mijack.ui.ImageDisplayActivity;

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

    public static String fileSuffix(File file) {
        String path = file.getPath();
        int lastIndexOf = path.lastIndexOf(".");
        if (lastIndexOf > 0) {
            return path.substring(lastIndexOf);
        }
        return null;
    }

    public static String base64Encode(String string) {
        return  new String(Base64.encode(string.getBytes(), Base64.NO_WRAP));
    }

    public static String base64Decode(String string) {
        return  new String(Base64.decode(string.getBytes(), Base64.NO_WRAP));
    }
}
