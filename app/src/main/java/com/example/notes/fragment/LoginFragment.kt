package com.example.notes.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.R
import com.example.notes.activity.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class LoginFragment : Fragment() {

    lateinit var sign_in_button : SignInButton


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        sign_in_button = view.findViewById(R.id.sign_in_button)
        MainActivity.background.visibility = View.INVISIBLE
        MainActivity.firstScreen.setBackgroundColor(Color.WHITE)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(activity as Context, gso);

        //Setting default size for sign-in Button
        sign_in_button.setSize(SignInButton.SIZE_STANDARD);


        //When clicked on Sign-in, Google will show various accounts for sign-in
        sign_in_button.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN)
        }


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == MainActivity.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
           updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.statusCode)

        }
    }


    //When sign-in is successful, we first edit the shared preferences and put the value of "isLoggedIn" as true
    //and then move from login fragment to home fragment
    private fun updateUI(acc : GoogleSignInAccount?){
        val fragHome = HomeFragment()
        MainActivity.sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
        MainActivity.acct = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext) as GoogleSignInAccount
        requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame,fragHome)
                .commit()
    }

}