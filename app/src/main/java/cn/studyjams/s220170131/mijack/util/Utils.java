package cn.studyjams.s220170131.mijack.util;

import android.content.Context;
import android.database.Cursor;

import java.io.Closeable;
import java.io.IOException;
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
}
