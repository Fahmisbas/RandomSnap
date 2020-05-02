package com.fahmisbas.randomsnap.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.fahmisbas.randomsnap.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ViewSnapActivity : AppCompatActivity() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        val imgSnapReceived = findViewById<ImageView>(R.id.img_snap_received)
        val tvMessageReceived = findViewById<TextView>(R.id.tv_message_received)

        tvMessageReceived.setText(intent.getStringExtra("message"))

        Glide.with(this)
            .load(intent.getStringExtra("imageUrl"))
            .into(imgSnapReceived)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("snaps")
            .child(intent.getStringExtra("snapKey")).removeValue()

        FirebaseStorage.getInstance().reference.child("images").child(intent.getStringExtra("imageName")).delete()
    }
}
