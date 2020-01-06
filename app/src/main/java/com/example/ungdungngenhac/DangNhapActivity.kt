package com.example.ungdungngenhac

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.facebook.*
import kotlinx.android.synthetic.main.activity_dang_nhap.*
import com.facebook.login.LoginResult
import com.facebook.login.LoginManager

import com.facebook.login.widget.LoginButton
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.logging.Logger
import com.facebook.Profile.getCurrentProfile
import com.facebook.GraphResponse
import com.facebook.GraphRequest
import org.json.JSONException
import com.facebook.Profile.getCurrentProfile
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task


class DangNhapActivity : AppCompatActivity() {

    //Login facebook
    private var callbackManager: CallbackManager? = null

    private var username: String? = null
    private var email: String? = null
    private var avatar: String? = null
    private var pre: SharedPreferences? = null

    //    Login Google
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_nhap)
        callbackManager = CallbackManager.Factory.create()

        //TODO: Login With Facebook
        login_img_fb.setOnClickListener(View.OnClickListener {
            // Login

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        var profile = Profile.getCurrentProfile()
                        username = profile.name
                        avatar = profile.getProfilePictureUri(80, 80).toString()
                        Log.d("avatar", avatar)
                        var intent: Intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(applicationContext, "Đăng nhập bằng facebook thành công!", Toast.LENGTH_SHORT)
                            .show()
                        restoredSharedpreference()
                        finish()
                    }

                    override fun onCancel() {
                        Log.d("MainActivity", "Facebook onCancel.")

                    }

                    override fun onError(error: FacebookException) {
                        Log.d("MainActivity", "Facebook onError.")

                    }
                })
        })


        //TODO: Login With Google
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("206660978551-ak4gonr8oqcbo93nsc5no6br26ondshf.apps.googleusercontent.com")
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        //onclick
        login_img_google.setOnClickListener {
            signIn()
        }

    }


    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //TODO : Lưu account vào sharepreferenced
    private fun restoredSharedpreference() {
        pre = getSharedPreferences("my_account", Context.MODE_PRIVATE)
        var edit = pre?.edit()
        edit!!.putBoolean("Status_login", true)
        edit!!.putString("username", username)
        edit!!.putString("avatar", avatar)
        edit.commit();
    }

    //TODO Nhận kết quả trả về khi user login
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            var task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.d("Account", "signInResult:failed code=" + e.getStatusCode());
        }

    }
    //TODO: cập nhật data user vào sharepreferenced và intent sang Main
    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            username = account.getDisplayName()
            avatar = account.getPhotoUrl().toString()
            Log.d("avatar google",username+"   "+avatar)
            restoredSharedpreference()
            val inten: Intent = Intent(this@DangNhapActivity, MainActivity::class.java)
            startActivity(inten)
            Toast.makeText(this, "Đăng nhập bằng google thành công!", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

}
