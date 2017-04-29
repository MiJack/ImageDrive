package cn.studyjams.s220170131.mijack.remote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.SparseArray;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.studyjams.s220170131.mijack.R;

/**
 * @author Mr.Yuan
 * @date 2017/4/29
 */
public class FirebaseManager {
    public static final String TASK_ID = "taskId";

    private FirebaseAuth firebaseAuth;
    ThreadPoolExecutor threadPool;
    private Context context;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    SparseArray<FirebaseUploadTask> taskSparseArray;

    public FirebaseManager(int nThreads) {
        taskSparseArray = new SparseArray<>();
        threadPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        firebaseAuth = FirebaseAuth.getInstance();
        if (!isLogin()) {
            return;
        }
        loadDatabase();
    }

    public void setCorePoolSize(int size) {
        threadPool.setCorePoolSize(size);
    }

    private void loadDatabase() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("images").child(uid);
        storageReference = FirebaseStorage.getInstance().getReference().child("images").child(uid);
    }

    public boolean isLogin() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void onCreate(Context context) {
        this.context = context;
    }

    public void submitImage(String path) {
        //计算文件的md5
        File file = new File(path);
        int uploadId = file.hashCode();

        FirebaseUploadTask uploadTask = new FirebaseUploadTask(this, uploadId, file);
        threadPool.execute(uploadTask);
        taskSparseArray.put(uploadId, uploadTask);
    }


    public void showPauseNotification(FirebaseUploadTask task) {
        File file = task.getFile();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(file.getName())
                .setAutoCancel(false)
                .addAction(-1, "cancel", FirebaseCloudService.cancelIntent(context, task))
                .addAction(-1, "resume", FirebaseCloudService.resumeIntent(context, task));

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(task.getId(), builder.build());
    }

    protected void showProgressNotification(FirebaseUploadTask task, long completedUnits, long totalUnits) {
        int percentComplete = 0;
        if (totalUnits > 0) {
            percentComplete = (int) (100 * completedUnits / totalUnits);
        }
        File file = task.getFile();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(file.getName())
                .setProgress(100, percentComplete, false)
                .setOngoing(true)
                .setAutoCancel(false)
                .addAction(-1, "cancel", FirebaseCloudService.cancelIntent(context, task))
                .addAction(-1, "pause", FirebaseCloudService.pauseIntent(context,task));
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(task.getId(), builder.build());
    }

    public void showFailureNotification(FirebaseUploadTask task, Exception result) {
        File file = task.getFile();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(result.getMessage())
                .setShowWhen(true)
                .setOngoing(true)
                .setAutoCancel(false)
                .addAction(-1, "cancel", FirebaseCloudService.cancelIntent(context,task))
                .addAction(-1, "retry", PendingIntent.getService(context, -1,
                        new Intent(context, FirebaseCloudService.class)
                                .putExtra(TASK_ID, task.getId())
                                .setAction(FirebaseCloudService.ACTION_RETRY_TASK), PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(task.getId(), builder.build());
    }

    public void showSuccessNotification(FirebaseUploadTask task) {
        File file = task.getFile();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(file.getName() + "上传成功")
                .setShowWhen(true)
                .setAutoCancel(false);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(task.getId(), builder.build());
    }

    public void onDestroy() {
        threadPool.shutdownNow();
        this.context = null;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public Context getContext() {
        return context;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public void pause(int taskId) {
        FirebaseUploadTask task = taskSparseArray.get(taskId);
        task.pause();
    }

    public void cancel(int taskId) {
        FirebaseUploadTask task = taskSparseArray.get(taskId);
        task.cancel();
        taskSparseArray.remove(taskId);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(taskId);
    }

    public void resume(int taskId) {
        FirebaseUploadTask task = taskSparseArray.get(taskId);
        task.resume();
    }

    public void retry(int taskId) {

    }
}
