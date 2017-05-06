package cn.studyjams.s2.sj20170131.mijack.firebase;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.studyjams.s2.sj20170131.mijack.database.DataBaseContentProvider;
import cn.studyjams.s2.sj20170131.mijack.database.DatabaseSQLiteOpenHelper;
import cn.studyjams.s2.sj20170131.mijack.entity.Image;
import cn.studyjams.s2.sj20170131.mijack.util.Utils;

public class FireBaseStorageService extends Service {
    public static final String TAG = "FireBaseStorageService";
    public static final String ACTION_ADD_TASK = "cn.studyjams.s2.sj20170131.mijack.remote.FireBaseStorageService.ACTION_ADD_TASK";
    public static final String ACTION_CANCEL_TASK = "cn.studyjams.s2.sj20170131.mijack.remote.FireBaseStorageService.ACTION_CANCEL_TASK";
    public static final String ACTION_PAUSE_TASK = "cn.studyjams.s2.sj20170131.mijack.remote.FireBaseStorageService.ACTION_PAUSE_TASK";
    public static final String ACTION_RESUME_TASK = "cn.studyjams.s2.sj20170131.mijack.remote.FireBaseStorageService.ACTION_RESUME_TASK";
    public static final String ACTION_RETRY_TASK = "cn.studyjams.s2.sj20170131.mijack.remote.FireBaseStorageService.ACTION_RETRY_TASK";

    public static final String IMAGE = "IMAGE";
    private static final String CLOUD_FILE_NAME = "CLOUD_FILE_NAME";
    FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private Map<String, StorageTask<UploadTask.TaskSnapshot>> map = new HashMap<>();
    Uri uri = Uri.parse("content://" + DataBaseContentProvider.AUTHORITIES + "/" + DatabaseSQLiteOpenHelper.TABLE_NAME);

    public FireBaseStorageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "请登录后重试", Toast.LENGTH_SHORT).show();
            return START_NOT_STICKY;
        }
        String action = intent.getAction();
        Log.d(TAG, "onStartCommand: action " + action);
        Image image = intent.getParcelableExtra(IMAGE);
        Log.d(TAG, "onStartCommand: image " + image);
        String cloudFileName = intent.getStringExtra(CLOUD_FILE_NAME);
        Log.d(TAG, "onStartCommand: cloudFileName "+cloudFileName);
        switch (action) {
            case ACTION_RETRY_TASK:
            case ACTION_ADD_TASK:
                addTask(image);
                break;
            case ACTION_CANCEL_TASK:
                if (cloudFileName != null) {
                    StorageTask<UploadTask.TaskSnapshot> storageTask =   map.remove(cloudFileName);
                    if (!storageTask.isComplete()) {
                        storageTask.cancel();
                    }
                    getContentResolver()
                            .delete(uri, DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                            , new String[]{cloudFileName});
                }
                break;

            case ACTION_PAUSE_TASK:
                if (cloudFileName != null) {
                    StorageTask<UploadTask.TaskSnapshot> storageTask = map.get(cloudFileName);
                    if (storageTask.isComplete()) {
                        storageTask.pause();
                    }
                }
                break;
            case ACTION_RESUME_TASK:
                if (cloudFileName != null) {
                    StorageTask<UploadTask.TaskSnapshot> storageTask = map.get(cloudFileName);
                    if (storageTask.isComplete()) {
                        storageTask.resume();
                    }
                }
                break;

        }
        return START_STICKY;
    }

    private void addTask(Image image) {
        //设备-path-md5.
        File file = new File(image.getPath());
        String md5 = Utils.fileMD5(file);
        String suffix = Utils.fileSuffix(file);
        String device = Build.DEVICE;
        String cloudFileName = Utils.base64Encode(device + "-" + image.getPath() + "-" + md5) + "." + suffix;
        System.out.println(cloudFileName);
        UploadUnit callBack = new UploadUnit(this, cloudFileName, image);
        StorageReference reference = firebaseStorage.getReference()
                .child("image").child(firebaseAuth.getCurrentUser().getUid());
        StorageTask<UploadTask.TaskSnapshot> storageTask = reference.child(cloudFileName).putFile(Uri.fromFile(file))
                .addOnProgressListener(callBack)
                .addOnSuccessListener(callBack)
                .addOnFailureListener(callBack)
                .addOnPausedListener(callBack);
        map.put(cloudFileName, storageTask);
    }

    public static PendingIntent cancelIntent(Context context, UploadUnit unit) {
        return PendingIntent.getService(context, -1,
                getIntent(context, unit).setAction(FireBaseStorageService.ACTION_CANCEL_TASK), PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static Intent getIntent(Context context, UploadUnit unit) {
        return new Intent(context, FireBaseStorageService.class)
                .putExtra(CLOUD_FILE_NAME, unit.getCloudFileName())
                .putExtra(IMAGE, unit.getImage());
    }

    public static PendingIntent resumeIntent(Context context, UploadUnit unit) {
        return PendingIntent.getService(context, -1,
                getIntent(context, unit)
                        .setAction(FireBaseStorageService.ACTION_RESUME_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent pauseIntent(Context context, UploadUnit unit) {
        return PendingIntent.getService(context, -1,
                getIntent(context, unit).setAction(FireBaseStorageService.ACTION_PAUSE_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent retryIntent(Context context, UploadUnit unit) {
        return PendingIntent.getService(context, -1,
                getIntent(context, unit)
                        .setAction(FireBaseStorageService.ACTION_RETRY_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
