package cn.studyjams.s220170131.mijack.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import cn.studyjams.s220170131.mijack.R;
import cn.studyjams.s220170131.mijack.base.BaseFragment;
import cn.studyjams.s220170131.mijack.remote.FirebaseManager;
import cn.studyjams.s220170131.mijack.util.Utils;

/**
 * @author Mr.Yuan
 * @date 2017/4/28
 */
public class BackUpFragment extends BaseFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_backup, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btn = (Button) view.findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                Toast.makeText(getActivity(), "upload", Toast.LENGTH_SHORT).show();
                String path = "/storage/emulated/0/tencent/MicroMsg/WeiXin/mmexport1493793151936.jpg";
                File file = new File(path);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String md5 = Utils.fileMD5(file);
                String suffix = Utils.fileSuffix(file);
                String cloudName = md5 + suffix;
                StorageReference reference =
                        storage.getReference()
                                .child("images")
                                .child(firebaseAuth.getCurrentUser().getUid())
                                .child(cloudName);
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setCustomMetadata("local path", Utils.base64Encode(file.getAbsolutePath()))
                        .setCustomMetadata("device", Utils.base64Encode(Build.PRODUCT.toString()))
                        .build();
                reference.putFile(Uri.fromFile(file),metadata)
                        .addOnProgressListener(getActivity(), new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                System.out.println("onProgress:" + taskSnapshot.getBytesTransferred() + "/" + taskSnapshot.getTotalByteCount());
                            }
                        }).addOnCompleteListener(getActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        System.out.println("onComplete");
                    }
                }).addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("onFailure");
                    }
                }).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("onSuccess");
                    }
                });
                break;
        }
    }
}
