package cn.studyjams.s2.sj20170131.mijack.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.database.DataBaseContentProvider;
import cn.studyjams.s2.sj20170131.mijack.database.DatabaseSQLiteOpenHelper;
import cn.studyjams.s2.sj20170131.mijack.entity.Image;

/**
 * @author Mr.Yuan
 * @date 2017/5/3
 */
public class UploadUnit implements OnProgressListener<UploadTask.TaskSnapshot>,
        OnSuccessListener<UploadTask.TaskSnapshot>, OnFailureListener, OnPausedListener<UploadTask.TaskSnapshot> {
    private static final String NOTIFICATION_GROUP_UPLOAD = "NOTIFICATION_GROUP_UPLOAD";
    private static final String NOTIFICATION_GROUP_PAUSE = "NOTIFICATION_GROUP_PAUSE";
    private static final String NOTIFICATION_GROUP_SUCCESS = "NOTIFICATION_GROUP_SUCCESS";
    private static final String NOTIFICATION_GROUP_FAILRUE = "NOTIFICATION_GROUP_FAILRUE";
    private static final int NOTIFICATION_GROUP_UPLOAD_ID = -1;
    private static final int NOTIFICATION_GROUP_PAUSE_ID = -2;
    private static final int NOTIFICATION_GROUP_SUCCESS_ID = -3;
    private static final int NOTIFICATION_GROUP_FAILRUE_ID = -4;
    private NotificationManager manager;
    private FireBaseStorageService context;
    private String cloudFileName;
    private Image image;
    private String fileExtensionName;
    private int taskId;
    Uri uri = Uri.parse("content://" + DataBaseContentProvider.AUTHORITIES + "/" + DatabaseSQLiteOpenHelper.TABLE_NAME);

    public UploadUnit(FireBaseStorageService context, String cloudFileName, Image image, String fileExtensionName) {
        this.context = context;
        this.cloudFileName = cloudFileName;
        this.image = image;
        this.fileExtensionName = fileExtensionName;
        this.taskId = System.identityHashCode(this);
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        ContentResolver contentResolver = context.getContentResolver();
        //查询是否存在
        Cursor cursor = contentResolver.query(uri,
                new String[]{"count(*) as count "},
                DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                , new String[]{cloudFileName}, null);
        if (cursor.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, Status.start.name());
            contentResolver.delete(uri, DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                    , new String[]{cloudFileName});
        }    //添加到数据库
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_FILE_EXTENSION_NAME, fileExtensionName);
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_NAME, image.getName());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_PATH, image.getPath());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_SIZE, image.getSize());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_WIDTH, image.getWidth());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_HEIGHT, image.getHeight());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME, cloudFileName);
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, Status.start.name());
        contentResolver.insert(uri, values);
    }

    public String getCloudFileName() {
        return cloudFileName;
    }

    @Override
    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        showProgressNotification(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
    }

    protected void showProgressNotification(long completedUnits, long totalUnits) {
        int percentComplete = 0;
        if (totalUnits > 0) {
            percentComplete = (int) (100 * completedUnits / totalUnits);
        }
        System.out.println(Thread.currentThread().getName());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name) + " Progress")
                .setContentText(image.getName())
                .setProgress(100, percentComplete, false)
                .setOngoing(true)
                .setGroup(NOTIFICATION_GROUP_UPLOAD)
                .setAutoCancel(false)
                .addAction(-1, "cancel", FireBaseStorageService.cancelIntent(context, this))
                .addAction(-1, "pause", FireBaseStorageService.pauseIntent(context, this));

        manager.notify(getTaskId(), builder.build());
        updateNotificationSummary(NOTIFICATION_GROUP_UPLOAD, NOTIFICATION_GROUP_UPLOAD_ID);
    }

    public void updateNotificationSummary(String groupKey, int groupId) {
        int numberOfNotifications = getNumberOfNotifications(groupKey, groupId);
        if (numberOfNotifications == 0) {
            return;
        }
        if (numberOfNotifications > 1) {
            String notificationContent = String.format("notifications %d", numberOfNotifications);
            final NotificationCompat.Builder NOTIFICATION_GROUP = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_icon)
                    .setStyle(new NotificationCompat.BigTextStyle().setSummaryText(notificationContent))
                    .setGroup(groupKey)
                    .setGroupSummary(true); //这句话必须和上面那句一起调用，否则不起作用
            final Notification notification = NOTIFICATION_GROUP.build();
            manager.notify(groupId, notification);
        } else {
            //移除归类
            manager.cancel(groupId);
        }

    }

    private int getNumberOfNotifications(String key, int groupId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //查询当前展示的所有通知的状态列表
            final StatusBarNotification[] activeNotifications = manager
                    .getActiveNotifications();
            int count = 0;
            for (StatusBarNotification notification : activeNotifications) {
                if (key.equals(notification.getGroupKey()) && notification.getId() != groupId) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    public int getTaskId() {
        return taskId;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        showSuccessNotification(taskSnapshot);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        showFailureNotification(e);
    }

    @Override
    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
        showPauseNotification();
    }

    public void showPauseNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name) + " Pause")
                .setContentText(image.getName())
                .setAutoCancel(false)
                .setGroup(NOTIFICATION_GROUP_PAUSE)
                .addAction(-1, "cancel", FireBaseStorageService.cancelIntent(context, this))
                .addAction(-1, "resume", FireBaseStorageService.resumeIntent(context, this));
        manager.notify(getTaskId(), builder.build());
        updateNotificationSummary(NOTIFICATION_GROUP_PAUSE, NOTIFICATION_GROUP_PAUSE_ID);
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, Status.pause.name());
        context.getContentResolver().update(uri, values, DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                , new String[]{cloudFileName});
    }

    public void showFailureNotification(Exception result) {

        System.out.println(Thread.currentThread().getName());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name) + " Failure")
                .setContentText(result.getMessage())
                .setShowWhen(true)
                .setGroup(NOTIFICATION_GROUP_FAILRUE)
                .setAutoCancel(true)
                .addAction(-1, "cancel", FireBaseStorageService.cancelIntent(context, this))
                .addAction(-1, "retry", FireBaseStorageService.retryIntent(context, this));
        manager.notify(getTaskId(), builder.build());
        updateNotificationSummary(NOTIFICATION_GROUP_FAILRUE, NOTIFICATION_GROUP_FAILRUE_ID);
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, Status.failure.name());
        context.getContentResolver().update(uri, values, DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                , new String[]{cloudFileName});
    }

    public void showSuccessNotification(UploadTask.TaskSnapshot taskSnapshot) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_backup)
                .setContentTitle(context.getString(R.string.app_name) + " Success")
                .setContentText(image.getName() + "上传成功")
                .setGroup(NOTIFICATION_GROUP_SUCCESS)
                .setShowWhen(true);
        manager.notify(getTaskId(), builder.build());
        System.out.println("showSuccessNotification");
        updateNotificationSummary(NOTIFICATION_GROUP_SUCCESS, NOTIFICATION_GROUP_SUCCESS_ID);
        ContentValues values = new ContentValues();
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_STATUS, Status.success.name());
        values.put(DatabaseSQLiteOpenHelper.Database.COLUMNS_URL,taskSnapshot.getDownloadUrl().toString());
        context.getContentResolver().update(uri, values, DatabaseSQLiteOpenHelper.Database.COLUMNS_GS_CLOUD_FILE_NAME + "=?"
                , new String[]{cloudFileName});
    }

    public enum Status {
        start, success, failure, pause
    }
}
