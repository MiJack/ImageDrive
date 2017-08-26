package cn.mijack.imagedrive.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.text.TextUtils
import java.io.*
import java.math.BigInteger
import java.nio.channels.FileChannel
import java.security.MessageDigest
import java.text.DateFormat
import java.util.*

/**
 * @author admin
 * @date 2017/8/26
 */
class Utils {
    companion object {
        fun <E> size(collection: Collection<E>?): Int {
            return collection?.size ?: 0
        }

        fun close(vararg closeables: Closeable) {
            if (closeables != null) {
                for (c: Closeable in closeables) {
                    try {
                        c?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun getScreenWidth(context: Context): Int {
            return context.resources.displayMetrics.widthPixels;
        }

        fun fileMD5(file: File): String {
            var value: String? = null
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(file)
                val byteBuffer = fis.channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
                val md5 = MessageDigest.getInstance("MD5")
                md5.update(byteBuffer)
                val bi: BigInteger = BigInteger(1, md5.digest())
                value = bi.toString(16)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (null != fis) {
                    try {
                        fis.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return value!!
        }

        fun fileExtensionName(file: File): String? {
////            String path = file.getPath()
//            var path :String=file.path
//            var lastIndexOf = file.path
//path.subSequence()
//            if (lastIndexOf > 0) {
//                return path.(lastIndexOf)
//            }
            return null
        }

        fun base64Encode(string: String): String? {
            return null
        }

        //
        fun base64Decode(string: String): String? {
            return null
        }
//

        fun isEmpty(uri: Uri): Boolean {
            return uri == null || TextUtils.isEmpty(uri.toString())
        }

        fun formatTime(time: Long): String {
            return DateFormat.getDateInstance().format(Date(time))
        }

        fun isIntentAvailable(context: Context, intent: Intent): Boolean {
            var packageManager = context.packageManager
            var list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            return list.size > 0
        }
    }
}