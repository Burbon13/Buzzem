package com.burbon13.buzzem.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Toast
import com.burbon13.buzzem.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import android.content.DialogInterface
import android.R.string.cancel
import android.util.Log


class LoginActivity : AppCompatActivity() {
    private var mAuth:FirebaseAuth? = null
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance()
        mAuth!!.addAuthStateListener {

        }

        ivQuestions.setOnClickListener {
            val builder = AlertDialog.Builder(ContextThemeWrapper(this,R.style.AlertDialogCustom))
//            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.login_question_title)
            builder.setCancelable(false)
            builder.setMessage(R.string.login_question_text)
                    .setPositiveButton("Got it ;)", DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                    })


            val alertDialog = builder.create()
            alertDialog.show()
        }

        tvSignUp.setOnClickListener {
            val signUpIntent = Intent(this, SignUpActivity::class.java)
            finish()
            //overridePendingTransition(0, 0)
            startActivity(signUpIntent)
        }
    }

    override fun onStart() {
        super.onStart()

        updateUI(mAuth?.currentUser)
    }

    fun updateUI(currentUser:FirebaseUser?) {

        Log.d(TAG, "updateUI, current user: " + currentUser.toString())

        if(currentUser != null) {

            if(currentUser.isEmailVerified)
                goToMainActivity()
            else {
                mAuth?.signOut()
                //Toast.makeText(applicationContext,R.string.wait_for_verif,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun loginEvent(view:View) {
        Log.d(TAG, "loginEvent")
        val email = etEmailSign.text.toString()
        val password = etPasswordSign.text.toString()


        //TODO:1) Understand this shit
        //TODO:2) Add loading image while loading account
        //Try to understand this part...OnCompleteListenr object?
        try {
            if(mAuth == null)
                Log.e(TAG, "mAuth is null")
            else
                Log.d(TAG, "mAuth is not null")
            mAuth?.signInWithEmailAndPassword(email,password)
                    ?.addOnCompleteListener(this, OnCompleteListener { task ->
                        Log.d(TAG, "signInWithEmailAndPassword completed")
                        when {
                            task.isSuccessful -> {
                                val cUser = mAuth?.currentUser
                                if(cUser?.isEmailVerified == false) {
                                    Toast.makeText(applicationContext, R.string.wait_for_verif,
                                            Toast.LENGTH_LONG).show()
                                    mAuth?.signOut()
                                } else {
                                    goToMainActivity()
                                }
                            }
                            else -> {
                                //TODO: Change to user friendly message and send raport
                                Toast.makeText(applicationContext,task.exception.toString(),Toast.LENGTH_LONG).show()
                            }
                        }
                    })
        } catch (ex:Exception) {
            //TODO: Change to user friendly message and send raport
            Toast.makeText(applicationContext,ex.message,Toast.LENGTH_LONG).show()
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
}
