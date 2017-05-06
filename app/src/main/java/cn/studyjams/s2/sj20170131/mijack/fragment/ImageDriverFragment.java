package cn.studyjams.s2.sj20170131.mijack.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.base.BaseFragment;

/**
 * @author Mr.Yuan
 * @date 2017/4/28
 */
public class ImageDriverFragment extends BaseFragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_driver, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnQuery = (Button) view.findViewById(R.id.btnQuery);
        btnQuery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReferenceFromUrl("gs://fir-studyjams.appspot.com");
        StorageReference child = reference.child("test");
        child.getMetadata()
                .addOnFailureListener(result-> System.out.println(result.getMessage()))
                .addOnSuccessListener(result -> {
            List<Uri> downloadUrls = result.getDownloadUrls();
            for (Uri uri : downloadUrls) {
                System.out.println(uri);
            }
        });
    }
}
