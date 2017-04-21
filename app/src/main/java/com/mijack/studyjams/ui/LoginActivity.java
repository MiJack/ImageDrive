package com.mijack.studyjams.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mijack.studyjams.R;
import com.mijack.studyjams.base.BaseActivity;
import com.mijack.studyjams.widget.IMEBlockLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    TextView title;
    CircleImageView avatar;
    CircleImageView selectAvatar;
    TextView info;
    TextInputLayout textInputLayout;
    TextView otherChoice;
    Button nextAction;
    ProgressBar progressBar;
    Toolbar toolbar;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = (auth) -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        avatar = (CircleImageView) findViewById(R.id.avatar);
        selectAvatar = (CircleImageView) findViewById(R.id.selectAvatar);
        info = (TextView) findViewById(R.id.info);
        IMEBlockLayout inputLayout = (IMEBlockLayout) findViewById(R.id.inputLayout);
        textInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout);
        otherChoice = (TextView) findViewById(R.id.otherChoice);
        nextAction = (Button) findViewById(R.id.nextAction);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.buttonA).setOnClickListener(this);
        findViewById(R.id.buttonB).setOnClickListener(this);
        findViewById(R.id.buttonC).setOnClickListener(this);
        findViewById(R.id.buttonD).setOnClickListener(this);
        findViewById(R.id.buttonE).setOnClickListener(this);

        avatar.setVisibility(View.GONE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                title.setText("登录");
                info.setText("使用你的邮箱进行登录");
                textInputLayout.setHint("email");
                otherChoice.setText("创建账号");
                nextAction.setText("下一步");
                textInputLayout.setEnabled(true);
                textInputLayout.requestFocus();
                otherChoice.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                selectAvatar.setVisibility(View.GONE);
                textInputLayout.setPasswordVisibilityToggleEnabled(false);
                break;
            case R.id.button2:
                selectAvatar.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                title.setText("登录");
                info.setText("使用你的邮箱进行登录");
                textInputLayout.setHint("email");
                nextAction.setText("下一步");
                textInputLayout.setEnabled(false);
                otherChoice.setEnabled(false);
                nextAction.setEnabled(false);
                textInputLayout.setPasswordVisibilityToggleEnabled(false);
//                textInputLayout.requestFocus();
//                otherChoice.setVisibility(View.VISIBLE);
                break;
            case R.id.button3:
                selectAvatar.setVisibility(View.GONE);
                progressBar.setVisibility(View.INVISIBLE);
                avatar.setVisibility(View.VISIBLE);
                title.setText("UserName xxx");
                info.setText("xxxx@xxx.com");
                textInputLayout.setHint("password");
                textInputLayout.getEditText().setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                textInputLayout.setPasswordVisibilityToggleEnabled(true);
                nextAction.setText("登录");
                textInputLayout.setEnabled(true);
                otherChoice.setEnabled(true);
                otherChoice.setText("忘记密码");
                nextAction.setEnabled(false);
                break;
            case R.id.button4:
                break;
            case R.id.buttonA:
                title.setText("创建账户");
                selectAvatar.setVisibility(View.GONE);
                info.setText("请输入你的nickname");
                avatar.setVisibility(View.GONE);
                textInputLayout.setHint("NickName");
                textInputLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                otherChoice.setVisibility(View.INVISIBLE);
                break;
            case R.id.buttonB:
                title.setText("设置Email");
                info.setText("请输入你的email");
                selectAvatar.setVisibility(View.GONE);
                avatar.setVisibility(View.GONE);
                textInputLayout.setVisibility(View.VISIBLE);
                textInputLayout.setHint("Email");
                otherChoice.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case R.id.buttonC:
                progressBar.setVisibility(View.VISIBLE);
                textInputLayout.setEnabled(false);
                nextAction.setEnabled(false);
                break;
            case R.id.buttonD:
                progressBar.setVisibility(View.VISIBLE);
                title.setText("xxxx(xxxx@xxx.xx)");
                info.setText("请选择你的avatar");
                avatar.setVisibility(View.GONE);
                otherChoice.setVisibility(View.INVISIBLE);
                textInputLayout.setVisibility(View.GONE);
                selectAvatar.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonE:
                title.setText("设置密码");
                avatar.setVisibility(View.VISIBLE);
                info.setText("请输入你的password");
                textInputLayout.setHint("password");
                textInputLayout.setVisibility(View.VISIBLE);
                selectAvatar.setVisibility(View.GONE);
                textInputLayout.setEnabled(true);
                break;
        }
    }
}
