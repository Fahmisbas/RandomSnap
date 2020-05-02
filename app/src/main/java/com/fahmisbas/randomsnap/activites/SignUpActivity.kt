package com.fahmisbas.randomsnap.activites

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.fahmisbas.randomsnap.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText

    private lateinit var btnEditProfilePicture : Button
    private lateinit var imageProfilePicture : CircleImageView
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var imageBitmap : Bitmap

    var imageUrl : String = "https://firebasestorage.googleapis.com/v0/b/random-snap.appspot.com/o/images%2Fprofile_pic%2Fprofile_image.png?alt=media&token=4ca41f9e-c68f-4800-aaa6-7a884f1256af"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        edtUsername = findViewById(R.id.edt_username)
        btnEditProfilePicture = findViewById(R.id.btn_edit_userprofile_picture)
        imageProfilePicture = findViewById(R.id.img_profile_picture)

        btnEditProfilePic()
    }

    private fun btnEditProfilePic(){
        btnEditProfilePicture.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                } else {
                    getPhoto()
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage = data?.data
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, selectedImage)
            imageProfilePicture.setImageBitmap(bitmap)
            imageBitmap = bitmap
        }
    }

    fun signUp(view: View) {
        if (edtEmail.text.isEmpty()){
            edtEmail.error = "Field cannot be left blank!"
        }else if (edtPassword.text.isEmpty()) {
            edtPassword.error = "Field cannot be left blank!"
        }else if (edtUsername.text.isEmpty()){
            edtUsername.error = "Field cannot be left blank!"
        }else {
            auth.createUserWithEmailAndPassword(
                edtEmail.text.toString(), edtPassword.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    uploadImage()

                    val makeChild = FirebaseDatabase.getInstance().reference.child("users").child(task.result!!.user!!.uid)
                    makeChild.child("email").setValue(edtEmail.text.toString())
                    makeChild.child("username").setValue(edtUsername.text.toString())
                    makeChild.child("profilePicUrl").setValue(imageUrl)
                    makeChild.child("desc").setValue("Send me cool picture!")
                    Toast.makeText(this, "Account is created!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Account is not created!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage() {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val imageName = UUID.randomUUID().toString() + ".jpg"

        val storagePrefs = FirebaseStorage.getInstance().reference.child("images/profile_pic").child(imageName)
        val uploadTask = FirebaseStorage.getInstance().reference.child("images/profile_pic").child(imageName)
            .putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(applicationContext,"Upload failure",Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            storagePrefs.downloadUrl.addOnSuccessListener {
                val downloadUrl = it
                imageUrl = downloadUrl.toString()
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(auth.currentUser!!.uid).child("profilePicUrl").setValue(downloadUrl.toString())
            }
        }
    }
}
