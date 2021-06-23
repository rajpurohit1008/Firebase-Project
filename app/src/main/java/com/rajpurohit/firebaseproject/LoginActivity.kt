package com.rajpurohit.firebaseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 123
    private lateinit var auth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    lateinit var signInButton: SignInButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progressBar = findViewById(R.id.progressBar)
        signInButton = findViewById(R.id.signInButton)
        auth = Firebase.auth
        val loginButton: Button = findViewById(R.id.loginButton)
        val login_usernameInput: EditText = findViewById(R.id.login_usernameInput)
        val login_passwordInput: EditText = findViewById(R.id.login_passwordInput)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        findViewById<SignInButton>(R.id.signInButton).setOnClickListener {
            signIn()
        }


        loginButton.setOnClickListener {
            if (TextUtils.isEmpty(login_usernameInput.text.toString())) {
                login_usernameInput.error = "Please enter username"
                return@setOnClickListener
            } else if (TextUtils.isEmpty(login_passwordInput.text.toString())) {
                login_passwordInput.error = "Please enter password"
                return@setOnClickListener
            }
            progressBar.visibility = View.VISIBLE
            loginButton.visibility = View.GONE
            auth.signInWithEmailAndPassword(login_usernameInput.text.toString(), login_passwordInput.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            startActivity(Intent(this, MainActivity::class.java))
                            Toast.makeText(this, "signInWithEmail:success", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                            loginButton.visibility = View.VISIBLE
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Authentication failed. ${it.exception}",
                                    Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                            loginButton.visibility = View.VISIBLE
                        }
                    }

        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            //  handleSignInResult(task)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in failed $e", Toast.LENGTH_SHORT).show()

            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        signInButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(
                                this,
                                "signInWithCredential:success ${user?.uid}",
                                Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        createFirestore()
                        signInButton.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    } else {
                        // If sign in fails, display a message to the user.
                        signInButton.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(this, "signInWithCredential:failure", Toast.LENGTH_SHORT)
                                .show()
                    }
                }

    }

    fun registerPage(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun createFirestore() {
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("Users")
        val currentuser = auth.currentUser
        if (currentuser != null) {
            val endUer = Model(
                    currentuser?.uid.toString(),
                    currentuser?.displayName.toString(),
                    currentuser?.photoUrl.toString()
            )
            endUer?.let {
                userCollection.document(currentuser?.uid.toString()).set(it)
            }
        }
    }

}