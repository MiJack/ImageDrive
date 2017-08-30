package cn.mijack.imagedrive.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.base.BaseActivity
import cn.mijack.imagedrive.util.TextHelper
import com.google.firebase.auth.*


class LoginActivity : BaseActivity(), View.OnClickListener {
    private var otherChoice: TextView? = null
    private var nextAction: Button? = null
    private var toolbar: Toolbar? = null
    private var progressBar: ProgressBar? = null
    private var info: TextView? = null
    private var emailLayout: TextInputLayout? = null
    private var passwordLayout: TextInputLayout? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var imm: InputMethodManager? = null
    internal var status = STATUS_1
    private var coordinatorLayout: CoordinatorLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            auth ->
            val user = auth.currentUser
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user!!.uid)
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
        }
        setContentView(R.layout.activity_login)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        coordinatorLayout = findViewById<View>(R.id.content) as CoordinatorLayout
        otherChoice = findViewById<View>(R.id.otherChoice) as TextView
        nextAction = findViewById<View>(R.id.nextAction) as Button
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        info = findViewById<View>(R.id.info) as TextView
        emailLayout = findViewById<View>(R.id.emailLayout) as TextInputLayout
        passwordLayout = findViewById<View>(R.id.passwordLayout) as TextInputLayout
        otherChoice!!.setOnClickListener(this)
        nextAction!!.setOnClickListener(this)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.nextAction -> {
                val email = TextHelper.getText(emailLayout!!)
                if (TextUtils.isEmpty(email)) {
                    emailLayout!!.error = getString(R.string.email_is_empty)
                    return
                }
                if (!TextHelper.isEmail(email!!)) {
                    emailLayout!!.error = getString(R.string.email_format_is_not_correct)
                    return
                }
                val password = TextHelper.getText(passwordLayout!!)
                if (TextUtils.isEmpty(password)) {
                    passwordLayout!!.error = getString(R.string.password_is_empty)
                    return
                }
                if (password!!.length < 6) {
                    passwordLayout!!.error = getString(R.string.password_too_short)
                    return
                }
                if (status == LOGIN) {
                    login(email, password)
                } else if (status == CREATE) {
                    create(email, password)
                }
            }
            R.id.otherChoice -> {
                invalidateOptionsMenu()
                if (status == LOGIN) {
                    status = CREATE
                    nextAction!!.setText(R.string.create_account)
                    otherChoice!!.setText(R.string.login)
                    info!!.setText(R.string.signin_with_your_email)
                } else if (status == CREATE) {
                    status = LOGIN
                    otherChoice!!.setText(R.string.create_account)
                    nextAction!!.setText(R.string.login)
                    info!!.setText(R.string.login_with_your_account)
                    emailLayout!!.requestFocus()
                }
            }
        }
    }

    private fun create(email: String, password: String) {
        mFirebaseAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    log("success:" + result.toString())
                    setResult(RESULT_NEW_ACCOUNT)
                    val user = result.user
                    user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(user.email).build())
                            .addOnSuccessListener { aVoid -> Toast.makeText(this@LoginActivity, R.string.set_user_name_please, Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { e -> Toast.makeText(this@LoginActivity, R.string.set_user_name, Toast.LENGTH_SHORT).show() }
                            .addOnCompleteListener { task -> finish() }

                }
                .addOnFailureListener { result ->
                    log("failure:" + result.toString())
                    if (result is FirebaseAuthWeakPasswordException) {
                        Snackbar.make(coordinatorLayout!!, R.string.firebase_auth_weak_password, Snackbar.LENGTH_SHORT).show()
                    } else if (result is FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(coordinatorLayout!!, R.string.firebase_auth_invalid_email, Snackbar.LENGTH_SHORT).show()
                    } else if (result is FirebaseAuthUserCollisionException) {
                        Snackbar.make(coordinatorLayout!!, R.string.firebase_auth_user_collision, Snackbar.LENGTH_SHORT).show()
                    }
                }
                .addOnCompleteListener { result -> log("complete:" + result.toString()) }
    }

    private fun log(result: String) {
        Log.d(TAG, "log: " + result)
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }

    private fun login(email: String, password: String) {
        log("login")
        mFirebaseAuth!!.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    log("success:" + result.toString())
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener { result ->
                    log("failure:" + result)
                    if (result is FirebaseAuthInvalidUserException) {
                        Snackbar.make(coordinatorLayout!!, R.string.firebase_auth_invalid_user, Snackbar.LENGTH_SHORT).show()
                    } else if (result is FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(coordinatorLayout!!, R.string.firebase_auth_password_invalid, Snackbar.LENGTH_SHORT).show()
                    }
                }
    }

    companion object {
        private val TAG = "LoginActivity"
        private val LOGIN = 1
        private val CREATE = 2
        val RESULT_LOGIN = 1
        val RESULT_NEW_ACCOUNT = 2

        private val STATUS_1 = 1
    }


}
