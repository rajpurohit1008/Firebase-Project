package com.rajpurohit.firebaseproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val firstName: TextView = findViewById(R.id.firstnameInput)
        val lastName: TextView = findViewById(R.id.lastnameInput)
        val email: TextView = findViewById(R.id.emailInput)
        auth = Firebase.auth
        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        val dataReference = firebaseDatabase.reference.child("User")
        val root = dataReference.child(auth.currentUser?.uid.toString())


        root.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                firstName.text = snapshot.child("firstname").value.toString()
                lastName.text = snapshot.child("lastname").value.toString()
                email.text = snapshot.child("email").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        findViewById<Button>(R.id.button).setOnClickListener {
            Firebase.auth.signOut()
            finish()
        }

    }
    
}