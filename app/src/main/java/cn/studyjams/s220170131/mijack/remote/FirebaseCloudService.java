package cn.studyjams.s220170131.mijack.remote;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import static cn.studyjams.s220170131.mijack.remote.FirebaseManager.TASK_ID;

public class FirebaseCloudService extends IntentService implements Handler.Callback {
    public static final String TAG = "FirebaseCloudService";
    public static final int UPLOAD_SINGLE_FILE = 1;
    public static final int UPLOAD_FOLDER = 2;
    public static final String PATH = "PATH";
    public static final String ACTION_CANCEL_TASK = "cn.studyjams.s220170131.mijack.remote.FirebaseCloudService.ACTION_CANCEL_TASK";
    public static final String ACTION_PAUSE_TASK = "cn.studyjams.s220170131.mijack.remote.FirebaseCloudService.ACTION_PAUSE_TASK";
    public static final String ACTION_RESUME_TASK = "cn.studyjams.s220170131.mijack.remote.FirebaseCloudService.ACTION_RESUME_TASK";
    public static final String ACTION_RETRY_TASK = "cn.studyjams.s220170131.mijack.remote.FirebaseCloudService.ACTION_RETRY_TASK";
    private Messenger messenger;
    private Handler handler;
    private FirebaseManager firebaseManager;


    public FirebaseCloudService() {
        super("FirebaseCloudService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        handler = new Handler(this);
        messenger = new Messenger(handler);
        firebaseManager = new FirebaseManager(5);
        firebaseManager.onCreate(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        int taskId = intent.getIntExtra(TASK_ID, -1);
        if (action == null) {
            return;
        }
        switch (action) {
            case ACTION_CANCEL_TASK:
                firebaseManager.cancel(taskId);
                break;
            case ACTION_PAUSE_TASK:
                firebaseManager.pause(taskId);
                break;
            case ACTION_RESUME_TASK:
                firebaseManager.resume(taskId);
                break;
            case ACTION_RETRY_TASK:
                firebaseManager.retry(taskId);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case UPLOAD_SINGLE_FILE:
                Bundle bundle = (Bundle) msg.obj;
                String path = bundle.getString(PATH);
                firebaseManager.submitImage(path);
                return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseManager.onDestroy();
    }

    public static PendingIntent cancelIntent(Context context, FirebaseUploadTask task) {
        return PendingIntent.getService(context, -1,
                new Intent(context, FirebaseCloudService.class)
                        .putExtra(TASK_ID, task.getId())
                        .setAction(FirebaseCloudService.ACTION_CANCEL_TASK), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public static PendingIntent resumeIntent(Context context, FirebaseUploadTask task) {
        return PendingIntent.getService(context, -1,
                new Intent(context, FirebaseCloudService.class)
                        .putExtra(TASK_ID, task.getId())
                        .setAction(FirebaseCloudService.ACTION_RESUME_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent pauseIntent(Context context, FirebaseUploadTask task) {
        return PendingIntent.getService(context, -1,
                new Intent(context, FirebaseCloudService.class)
                        .putExtra(TASK_ID, task.getId())
                        .setAction(FirebaseCloudService.ACTION_PAUSE_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
