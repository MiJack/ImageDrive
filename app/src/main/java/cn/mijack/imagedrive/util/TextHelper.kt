package cn.mijack.imagedrive.util

import android.support.design.widget.TextInputLayout
import android.widget.EditText
import java.util.regex.Pattern


/**
 * @author Mr.Yuan
 * *
 * @date 2017/4/18
 */
class TextHelper {
    companion object {
        fun getText(textInputLayout: TextInputLayout): String? {
            val editText = textInputLayout.editText
            return getText(editText)
        }

        fun getText(editText: EditText?): String? {
            if (editText == null || editText.text == null) {
                return null
            }
            return editText.text.toString()
        }

        val EMAIL = Pattern.compile("^\\w+@\\w+(?:\\.\\w+)+$")
        fun isEmail(email: String): Boolean {
            return EMAIL.matcher(email).matches()
        }
    }
}
