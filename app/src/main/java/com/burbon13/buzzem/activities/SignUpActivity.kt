package com.burbon13.buzzem.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.burbon13.buzzem.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sing_up.*

class SignUpActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        mAuth = FirebaseAuth.getInstance()

        tvLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            finish()
            //overridePendingTransition(0, 0)
            startActivity(loginIntent)
        }


    }

    fun signUpEvent(view: View) {
        val password1 = etPasswordSign.text.toString()
        val password2 = etPasswordConfirm.text.toString()
        val email = etEmailSign.text.toString()

        if(!password1.equals(password2)) {
            Toast.makeText(applicationContext, R.string.passwords_not_equal,Toast.LENGTH_LONG).show()
            return
        }

        try {
            mAuth?.createUserWithEmailAndPassword(email,password1)
                    ?.addOnCompleteListener(this, OnCompleteListener { task ->
                        when {
                            task.isSuccessful -> {
                                verifyUserViaEmail()
                                goToLoginActivity()
                            }
                            else -> {
                                //TODO: Show user friendly message and send report
                               verifyUserViaEmail()
                            }
                        }
                    })
        } catch (ex:Exception) {
            Toast.makeText(applicationContext,ex.message,Toast.LENGTH_LONG).show()
        }
    }

    val TAG = "SIGN_UP_DB"

    private fun verifyUserViaEmail() {
        Log.d(TAG, "verifyUserViaEmail()")
        val user = mAuth?.currentUser
        //Log.d(TAG, user?.uid)
       // Log.d(TAG, user?.email)

        user?.sendEmailVerification()
                ?.addOnCompleteListener(OnCompleteListener { taskEmail ->
                    when {
                        taskEmail.isSuccessful -> {
                            Toast.makeText(applicationContext, R.string.email_sent,
                                    Toast.LENGTH_LONG).show()
                            addUserToDatabase(user.uid,user.email)
                        }
                        else -> {
                            Toast.makeText(applicationContext,
                                    taskEmail.exception.toString(),Toast.LENGTH_LONG)
                                    .show()
                        }
                    }
                })
    }

    fun addUserToDatabase(uid:String, email:String?) {

        if(email == null) {
            Toast.makeText(this,"Email is null", Toast.LENGTH_LONG).show()
            return
        }

        val myRef = FirebaseDatabase.getInstance().getReference()
       // myRef.child("users").child(uid).setValue(email
        myRef.child("users").child(uid).child("email").setValue(email)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        //Toast.makeText(applicationContext,"DATABASE WORKED", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, task.exception.toString(),
                                Toast.LENGTH_LONG).show()
                    }

                }
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        finish()
        startActivity(intent)
    }
}
