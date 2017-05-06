package cn.studyjams.s2.sj20170131.mijack.util;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

/**
 * @author Mr.Yuan
 * @date 2017/4/18
 */
public class TextHelper {
    public static String getText(TextInputLayout textInputLayout) {
        EditText editText = textInputLayout.getEditText();
        return getText(editText);
    }

    public static String getText(TextView editText) {
        if (editText == null) return null;
        if (editText.getText() == null) return null;
        return editText.getText().toString();
    }

    public static final Pattern EMAIL = Pattern.compile("^\\w+@\\w+(?:\\.\\w+)+$");

    public static boolean isEmail(String email) {
        return EMAIL.matcher(email).matches();
    }
}
