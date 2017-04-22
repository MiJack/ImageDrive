package com.mijack.studyjams.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

/**
 * @author Mr.Yuan
 * @date 2017/4/21
 */
public class IMEBlockLayout extends LinearLayout {
    OnDispatchKeyEventPreImeListener onDispatchKeyEventPreImeListener;

    public IMEBlockLayout(@NonNull Context context) {
        super(context);
    }

    public IMEBlockLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IMEBlockLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public IMEBlockLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (onDispatchKeyEventPreImeListener != null) {
            return onDispatchKeyEventPreImeListener.dispatchKeyEventPreIme(event);
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public void setOnDispatchKeyEventPreImeListener(OnDispatchKeyEventPreImeListener onDispatchKeyEventPreImeListener) {
        this.onDispatchKeyEventPreImeListener = onDispatchKeyEventPreImeListener;
    }

    public interface OnDispatchKeyEventPreImeListener {
        boolean dispatchKeyEventPreIme(KeyEvent event);
    }
}
