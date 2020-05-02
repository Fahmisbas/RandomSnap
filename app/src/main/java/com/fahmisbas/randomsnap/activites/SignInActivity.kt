package com.fahmisbas.randomsnap.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.fahmisbas.randomsnap.R
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)

        if (auth.currentUser != null) {
            login();
        }
    }

    fun signIn(view: View) {
        if (edtEmail.text.isEmpty()) {
            edtEmail.error = "Field cannot be left blank!"
        } else if (edtPassword.text.isEmpty()) {
            edtPassword.error = "Field cannot be left blank!"
        } else {
            auth.signInWithEmailAndPassword(edtEmail.text.toString(), edtPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        login()
                    } else {
                        Toast.makeText(this, "email or password is incorrect", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun login() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    fun createAccount(view: View) {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        auth.signOut()
        super.onBackPressed()
    }

}
