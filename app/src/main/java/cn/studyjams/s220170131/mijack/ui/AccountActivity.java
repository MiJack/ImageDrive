package cn.studyjams.s220170131.mijack.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import cn.studyjams.s220170131.mijack.R;
import cn.studyjams.s220170131.mijack.base.BaseActivity;
import cn.studyjams.s220170131.mijack.util.TextHelper;
import cn.studyjams.s220170131.mijack.widget.IMEBlockLayout;

public class AccountActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AccountActivity";
    private static final int LOGIN = 1;
    private static final int CREATE = 2;
    TextView otherChoice;
    Button nextAction;
    Toolbar toolbar;

    ProgressBar progressBar;
    TextView info;
    IMEBlockLayout inputLayout;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;


    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    InputMethodManager imm;
    int status = STATUS_1;

    private static final int STATUS_1 = 1;
    private static final int STATUS_2 = 2;
    private static final int STATUS_3 = 3;
    private static final int STATUS_4 = 4;
    private static final int STATUS_CREATE_ACCOUNT = 5;
    private static final int STATUS_6 = 6;
    private static final int STATUS_7 = 7;
    private static final int STATUS_8 = 8;
    private static final int STATUS_9 = 9;
    private Button button;

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
        otherChoice = (TextView) findViewById(R.id.otherChoice);
        nextAction = (Button) findViewById(R.id.nextAction);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        info = (TextView) findViewById(R.id.info);
        inputLayout = (IMEBlockLayout) findViewById(R.id.inputLayout);
        emailLayout = (TextInputLayout) findViewById(R.id.emailLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        otherChoice.setOnClickListener(this);
        nextAction.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account, menu);
        MenuItem item = menu.findItem(R.id.actionResetPassword);
        item.setVisible(status == LOGIN);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextAction:
                if (status == LOGIN) {
                    login();
                } else if (status == CREATE) {
                    create();
                }
                break;
            case R.id.otherChoice:
                invalidateOptionsMenu();
                if (status == LOGIN) {
                    status = CREATE;
                    nextAction.setText("创建账号");
                    otherChoice.setText("登录");
                } else if (status == CREATE) {
                    status = LOGIN;
                    otherChoice.setText("创建账号");
                    nextAction.setText("登录");
                    emailLayout.requestFocus();
                }
                break;
        }
    }

    private void create() {
        String email = TextHelper.getText(emailLayout);
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("请输入Email");
            return;
        }
        if (!TextHelper.isEmail(email)) {
            emailLayout.setError("Email 格式不正确");
            return;
        }
        String password = TextHelper.getText(passwordLayout);
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("请输入Password");
            return;
        }
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    //todo 检查proile
                    log("success:" + result.toString());

                })
                .addOnFailureListener(result -> {
                    log("failure:" + result.toString());
                })
                .addOnCompleteListener(result -> {
                    log("complete:" + result.toString());
                });
    }

    private void log(String result) {
        Log.d(TAG, "log: " + result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    private void login() {
        String email = TextHelper.getText(emailLayout);
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("请输入Email");
            return;
        }
        if (!TextHelper.isEmail(email)) {
            emailLayout.setError("Email 格式不正确");
            return;
        }
        String password = TextHelper.getText(passwordLayout);
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("请输入Password");
            return;
        }
        log("login");
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    log("success:" + result.toString());
                })
                .addOnFailureListener(result -> {
                    log("failure:" + result.toString());
                })
                .addOnCompleteListener(result -> {
                    log("complete:" + result.toString());
                    setResult(RESULT_OK);
                    finish();
                });
    }


}
