package cn.studyjams.s220170131.mijack.remote;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

import cn.studyjams.s220170131.mijack.util.Utils;

/**
 * @author Mr.Yuan
 * @date 2017/4/29
 */
public class FirebaseUploadTask implements Runnable {
    private static final String TAG = "FirebaseUploadTask";
    private int id;
    private File file;
    private FirebaseManager firebaseManager;
    private String cloudName;
    private StorageTask<UploadTask.TaskSnapshot> storageTask;

    public FirebaseUploadTask(FirebaseManager firebaseManager, int id, File file) {
        this.id = id;
        this.file = file;
        this.firebaseManager = firebaseManager;
    }

    @Override
    public void run() {
        //计算md5;
        String md5 = Utils.fileMD5(file);
        String suffix = Utils.fileSuffix(file);
        cloudName = md5 + suffix;
        StorageReference reference = firebaseManager.getStorageReference().child(cloudName);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("local path", file.getAbsolutePath())
                .setCustomMetadata("device", Build.PRODUCT.toString())
                .build();
        ThreadPoolExecutor threadPool = firebaseManager.getThreadPool();
        storageTask = reference.putFile(Uri.fromFile(file), metadata)
                .addOnProgressListener(threadPool,
                        taskSnapshot -> {
                            Log.d(TAG, "Progress: " + file.getName() + "\t" + taskSnapshot.getBytesTransferred() + "/" + taskSnapshot.getTotalByteCount());
                            firebaseManager.showProgressNotification(this, taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
                        })
                .addOnPausedListener(threadPool, taskSnapshot -> firebaseManager.showPauseNotification(FirebaseUploadTask.this))
                .addOnFailureListener(threadPool, result -> {
                    firebaseManager.showFailureNotification(FirebaseUploadTask.this, result);
                })
                .addOnSuccessListener(threadPool, result -> {
                    firebaseManager.showSuccessNotification(FirebaseUploadTask.this);
                });
    }

    public int getId() {
        return id;
    }

    public File getFile() {
        return file;
    }

    public FirebaseManager getFirebaseManager() {
        return firebaseManager;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void pause() {
        storageTask.pause();
    }

    public void cancel() {
        storageTask.cancel();
    }

    public void resume() {
        storageTask.resume();
    }
}
