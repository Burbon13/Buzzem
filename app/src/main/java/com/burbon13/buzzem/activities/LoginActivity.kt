package com.burbon13.buzzem.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.burbon13.buzzem.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var mAuth:FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance()

        ivQuestions.setOnClickListener {
            //TODO: Make pop-up text
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
        val email = etEmailSign.text.toString()
        val password = etPasswordSign.text.toString()

        //TODO:1) Understand this shit
        //TODO:2) Add loading image while loading account
        //Try to understand this part...OnCompleteListenr object?
        try {
            mAuth?.signInWithEmailAndPassword(email,password)
                    ?.addOnCompleteListener(this, OnCompleteListener { task ->
                        when {
                            task.isSuccessful -> {

                                val cUser = mAuth?.currentUser
                                if(cUser?.isEmailVerified == false) {
                                    Toast.makeText(applicationContext, R.string.wait_for_verif,
                                            Toast.LENGTH_LONG)
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