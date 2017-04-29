package cn.studyjams.s220170131.mijack.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.studyjams.s220170131.mijack.remote.FirebaseCloudService;

/**
 * @author Mr.Yuan
 * @date 2017/4/16
 */
public class BaseActivity extends AppCompatActivity implements ServiceConnection, Handler.Callback {
    Messenger localMessenger;
    Messenger remoteMessenger;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(this);
        localMessenger = new Messenger(handler);
        startService(new Intent(this, FirebaseCloudService.class));
        bindService(new Intent(this, FirebaseCloudService.class), this, Context.BIND_AUTO_CREATE);
    }

    protected void uploadImage(String path) {
        Bundle bundle =new Bundle();
        bundle.putString(FirebaseCloudService.PATH,path);
        tellService(bundle, FirebaseCloudService.UPLOAD_SINGLE_FILE);
    }


    protected void uploadFolder(String path) {
        Bundle bundle =new Bundle();
        bundle.putString(FirebaseCloudService.PATH,path);
        tellService(bundle, FirebaseCloudService.UPLOAD_FOLDER);
    }

    private void tellService(Parcelable parcelable, int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = parcelable;
        try {
            remoteMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        remoteMessenger = new Messenger(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
        remoteMessenger = null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}
