package cn.mijack.imagedrive.componment

import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v13.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.mijack.imagedrive.R
import cn.mijack.imagedrive.ui.LoginActivity
import cn.mijack.imagedrive.ui.MainActivity
import cn.mijack.imagedrive.ui.ProfileActivity
import cn.mijack.imagedrive.util.Utils
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

/**
 * @author Mr.Yuan
 * *
 * @date 2017/4/18
 */
class NavigationHeaderView(internal var activity: MainActivity, private val navigationView: NavigationView) : View.OnClickListener, FirebaseAuth.AuthStateListener {
    internal var profileView: ImageView
    internal var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    internal var nickName: TextView
    internal var email: TextView

    init {
        firebaseAuth.addAuthStateListener(this)
        if (navigationView.headerCount == 0) {
            throw IllegalArgumentException("NavigationView has headView")
        }
        val headerView = navigationView.getHeaderView(0)
        profileView = headerView.findViewById<View>(R.id.profile_image) as ImageView
        nickName = headerView.findViewById<View>(R.id.nickName) as TextView
        email = headerView.findViewById<View>(R.id.email) as TextView
        profileView.setOnClickListener(this)
    }

    fun loadLoginInfo() {
        val menu = navigationView.menu
        val currentUser = firebaseAuth.currentUser
        menu.findItem(R.id.actionLogout).isVisible = currentUser != null
        menu.findItem(R.id.actionProfile).isVisible = currentUser != null
        menu.findItem(R.id.actionDriver).isVisible = currentUser != null
        if (currentUser == null) {
            email.text = ""
            nickName.text = ""
        } else {
            email.text = currentUser.email
            nickName.text = currentUser.displayName
        }
        if (currentUser == null || Utils.isEmpty(currentUser.photoUrl!!)) {
            profileView.setImageResource(R.drawable.ic_empty_profile)
        } else {
            Glide.with(profileView.context).load(currentUser.photoUrl).into(profileView)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.profile_image ->
                //
                if (!isLogin) {
                    val intent = Intent(activity, LoginActivity::class.java)
                    activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_LOGIN)
                } else {
                    startProfileActivity()
                }
        }

    }

    val isLogin: Boolean
        get() = firebaseAuth.currentUser != null

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        loadLoginInfo()
    }

    fun startProfileActivity() {
        val intent = Intent(activity, ProfileActivity::class.java)
        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                Pair<View, String>(profileView, "profile"))

        ActivityCompat.startActivityForResult(activity, intent, MainActivity.REQUEST_CODE_PROFILE, activityOptions.toBundle())
    }
}
