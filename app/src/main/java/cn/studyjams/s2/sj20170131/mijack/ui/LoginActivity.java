package cn.studyjams.s2.sj20170131.mijack.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import cn.studyjams.s2.sj20170131.mijack.R;
import cn.studyjams.s2.sj20170131.mijack.base.BaseActivity;
import cn.studyjams.s2.sj20170131.mijack.util.TextHelper;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final int LOGIN = 1;
    private static final int CREATE = 2;
    public static final int RESULT_LOGIN = 1;
    public static final int RESULT_NEW_ACCOUNT = 2;
    private TextView otherChoice;
    private Button nextAction;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView info;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private InputMethodManager imm;
    int status = STATUS_1;

    private static final int STATUS_1 = 1;
    private CoordinatorLayout coordinatorLayout;

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
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.content);
        otherChoice = (TextView) findViewById(R.id.otherChoice);
        nextAction = (Button) findViewById(R.id.nextAction);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        info = (TextView) findViewById(R.id.info);
        emailLayout = (TextInputLayout) findViewById(R.id.emailLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        otherChoice.setOnClickListener(this);
        nextAction.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextAction:
                String email = TextHelper.getText(emailLayout);
                if (TextUtils.isEmpty(email)) {
                    emailLayout.setError(getString(R.string.email_is_empty));
                    return;
                }
                if (!TextHelper.isEmail(email)) {
                    emailLayout.setError(getString(R.string.email_format_is_not_correct));
                    return;
                }
                String password = TextHelper.getText(passwordLayout);
                if (TextUtils.isEmpty(password)) {
                    passwordLayout.setError(getString(R.string.password_is_empty));
                    return;
                }
                if (password.length() < 6) {
                    passwordLayout.setError(getString(R.string.password_too_short));
                    return;
                }
                if (status == LOGIN) {
                    login(email, password);
                } else if (status == CREATE) {
                    create(email, password);
                }
                break;
            case R.id.otherChoice:
                invalidateOptionsMenu();
                if (status == LOGIN) {
                    status = CREATE;
                    nextAction.setText(R.string.create_account);
                    otherChoice.setText(R.string.login);
                    info.setText(R.string.signin_with_your_email);
                } else if (status == CREATE) {
                    status = LOGIN;
                    otherChoice.setText(R.string.create_account);
                    nextAction.setText(R.string.login);
                    info.setText(R.string.login_with_your_account);
                    emailLayout.requestFocus();
                }
                break;
        }
    }

    private void create(String email, String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    log("success:" + result.toString());
                    setResult(RESULT_NEW_ACCOUNT);
                    FirebaseUser user = result.getUser();
                    user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(user.getEmail()).build())
                            .addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, R.string.set_user_name_please, Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, R.string.set_user_name, Toast.LENGTH_SHORT).show())
                            .addOnCompleteListener(task -> finish());

                })
                .addOnFailureListener(result -> {
                    log("failure:" + result.toString());
                    if (result instanceof FirebaseAuthWeakPasswordException) {
                        Snackbar.make(coordinatorLayout, R.string.firebase_auth_weak_password, Snackbar.LENGTH_SHORT).show();
                    } else if (result instanceof FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(coordinatorLayout, R.string.firebase_auth_invalid_email, Snackbar.LENGTH_SHORT).show();
                    } else if (result instanceof FirebaseAuthUserCollisionException) {
                        Snackbar.make(coordinatorLayout, R.string.firebase_auth_user_collision, Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(result -> {
                    log("complete:" + result.toString());
                });
    }

    private void log(String result) {
        Log.d(TAG, "log: " + result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    private void login(String email, String password) {
        log("login");
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    log("success:" + result.toString());
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(result -> {
                    log("failure:" + result);
                    if (result instanceof FirebaseAuthInvalidUserException) {
                        Snackbar.make(coordinatorLayout, R.string.firebase_auth_invalid_user, Snackbar.LENGTH_SHORT).show();
                    } else if (result instanceof FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(coordinatorLayout, R.string.firebase_auth_password_invalid, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }


}
