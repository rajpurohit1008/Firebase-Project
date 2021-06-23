package com.rajpurohit.firebaseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        val dataReference = firebaseDatabase.reference.child("User")

        val registerButton: Button = findViewById(R.id.registerButton)
        val firstnameInput: EditText = findViewById(R.id.firstnameInput)
        val lastnameInput: EditText = findViewById(R.id.lastnameInput)
        val usernameInput: EditText = findViewById(R.id.usernameInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)

        registerButton.setOnClickListener {

            if (TextUtils.isEmpty(firstnameInput.text.toString())) {
                firstnameInput.error = "Please enter first name"
                return@setOnClickListener
            } else if (TextUtils.isEmpty(lastnameInput.text.toString())) {
                lastnameInput.error = "Please enter last name"
                return@setOnClickListener
            } else if (TextUtils.isEmpty(usernameInput.text.toString())) {
                usernameInput.error = "Please enter user name"
                return@setOnClickListener
            } else if (TextUtils.isEmpty(passwordInput.text.toString())) {
                passwordInput.error = "Please enter password"
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(
                    usernameInput.text.toString(),
                    passwordInput.text.toString()
            )
                    .addOnCompleteListener {
                        val root = dataReference.child(auth.currentUser?.uid.toString())
                        if (it.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            root.child("email").setValue(usernameInput.text.toString())
                            root.child("firstname").setValue(firstnameInput.text.toString())
                            root.child("lastname").setValue(lastnameInput.text.toString())
                            val user = auth.currentUser
                            Toast.makeText(this, "${user?.uid}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                    baseContext, "Authentication failed. ${it.exception}",
                                    Toast.LENGTH_SHORT
                            ).show()

                        }
                    }



        }
    }

    fun loginBack(view: View) {
        onBackPressed()
    }
}
