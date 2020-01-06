package com.example.ungdungngenhac

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount





class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long=3000 // 3 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            var pre:SharedPreferences = getSharedPreferences("my_account",MODE_PRIVATE)
            var status:Boolean=pre.getBoolean("Status_login",false)
            if(status) {
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, DangNhapActivity::class.java))
            }

            // close this activity
            finish()
        }, SPLASH_TIME_OUT)
    }
}
