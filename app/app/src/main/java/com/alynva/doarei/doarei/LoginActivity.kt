package com.alynva.doarei.doarei

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password



class LoginActivity : AppCompatActivity() {

    companion object {
        var mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        new_account_link.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            loginFirebase()
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            startMainActivity()
        }
    }

    private fun loginFirebase() {
        val email = ipt_login_user.text.toString()
        val password = ipt_login_pass.text.toString()
        if (!email.isBlank() && !password.isBlank()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val user = mAuth.currentUser
                            startMainActivity()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }

                        // ...
                    }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
