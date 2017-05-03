package cn.studyjams.s220170131.mijack.remote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
    private static final String NOTIFICATION_GROUP_UPLOAD = "NOTIFICATION_GROUP_UPLOAD";
    private static final String NOTIFICATION_GROUP_UPLOAD_FAILURE = "NOTIFICATION_GROUP_UPLOAD_FAILURE";
    private static final String NOTIFICATION_GROUP_UPLOAD_SUCCESS = "NOTIFICATION_GROUP_UPLOAD_SUCCESS";

    private FirebaseAuth firebaseAuth;
    ThreadPoolExecutor threadPool;
    private Context context;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    Map<Integer, FirebaseUploadTask> taskSparseArray;

    public FirebaseManager(int nThreads) {
        taskSparseArray = new HashMap<>();
        threadPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        firebaseAuth = FirebaseAuth.getInstance();
        if (!isLogin()) {
            return;
        }
        loadDatabase();
    }

    public void setCorePoolSize(int size) {
        if (size != threadPool.getCorePoolSize()) {
            threadPool.setCorePoolSize(size);
        }
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
//        uploadTask.run();
        taskSparseArray.put(uploadId, uploadTask);
    }


    public void showPauseNotification(FirebaseUploadTask task) {
        File file = task.getFile();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name)+" Pause")
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
        System.out.println(Thread.currentThread().getName());
        File file = task.getFile();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name)+" Progress")
                .setContentText(file.getName())
                .setProgress(100, percentComplete, false)
                .setOngoing(true)
                .setGroup(NOTIFICATION_GROUP_UPLOAD)
                .setGroupSummary(true)
                .setAutoCancel(false)
                .addAction(-1, "cancel", FirebaseCloudService.cancelIntent(context, task))
                .addAction(-1, "pause", FirebaseCloudService.pauseIntent(context, task));
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(task.getId(), builder.build());
    }

    public void showFailureNotification(FirebaseUploadTask task, Exception result) {

        System.out.println(Thread.currentThread().getName());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name)+" Failure")
                .setContentText(result.getMessage())
                .setShowWhen(true)
                .setGroup(NOTIFICATION_GROUP_UPLOAD_FAILURE)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .addAction(-1, "cancel", FirebaseCloudService.cancelIntent(context, task))
                .addAction(-1, "retry", FirebaseCloudService.retryIntent(context, task));

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(task.getId(), builder.build());
    }

    public void showSuccessNotification(FirebaseUploadTask task) {

        System.out.println(Thread.currentThread().getName());
        File file = task.getFile();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name)+" Success")
                .setContentText(file.getName() + "上传成功")
                .setGroup(NOTIFICATION_GROUP_UPLOAD_SUCCESS)
                .setGroupSummary(true)
                .setShowWhen(true);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(task.getId(), builder.build());
//        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
        System.out.println("showSuccessNotification");
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
//        showPauseNotification(task);
    }

    public void cancel(int taskId) {
        FirebaseUploadTask task = taskSparseArray.get(taskId);
        if (task == null) {
            return;
        }
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

    public void retry(String path) {
        submitImage(path);
    }
}
