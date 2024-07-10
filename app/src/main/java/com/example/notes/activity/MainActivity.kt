package com.example.notes.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.example.notes.fragment.HomeFragment
import com.example.notes.fragment.LoginFragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient


class MainActivity : AppCompatActivity() {

    companion object{
        val RC_SIGN_IN = 123
        lateinit var sharedPreferences: SharedPreferences
        lateinit var acct : GoogleSignInAccount
        lateinit var firstScreen : RelativeLayout
        lateinit var background : ImageView
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(getString(R.string.login_preference), Context.MODE_PRIVATE)
        firstScreen = findViewById(R.id.rlFirst)
        background =  findViewById(R.id.imgFirst)



        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)


        val fragLogin = LoginFragment()
        val fragHome = HomeFragment()

        //If logged in, it will go to home fragment else to login fragment
        if(isLoggedIn)
        {
            Handler().postDelayed({
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,fragHome)
                        .commit()
            },2000)

        }
        else
        {
            Handler().postDelayed({
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,fragLogin)
                        .commit()
            },2000)

        }

    }


    override fun onBackPressed() {
        //Maintaining count of total backStackEntry so that onBackPressed fuction is implemented
        val count = supportFragmentManager.backStackEntryCount
        println(count)
        if(count==0)
        {
            super.onBackPressed()
        }
        else
        {
            supportFragmentManager.popBackStack()
        }

    }


}