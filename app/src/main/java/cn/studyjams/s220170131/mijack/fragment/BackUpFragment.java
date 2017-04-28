package cn.studyjams.s220170131.mijack.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.studyjams.s220170131.mijack.R;
import cn.studyjams.s220170131.mijack.base.BaseFragment;

/**
 * @author Mr.Yuan
 * @date 2017/4/28
 */
public class BackUpFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_backup,container,false);
    }
}
