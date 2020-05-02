package com.fahmisbas.randomsnap.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fahmisbas.randomsnap.activites.ChooseUserActivity
import com.fahmisbas.randomsnap.R
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class AddSnapFragment : Fragment() {

    lateinit var imgSelected : ImageView
    lateinit var btnAddSnap : Button
    lateinit var btnSendTo : Button
    lateinit var edtMessage : EditText


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_addsnap,container,false)

        imgSelected = view.findViewById(R.id.img_selected)
        btnAddSnap = view.findViewById(R.id.btn_addSnap)
        btnSendTo = view.findViewById(R.id.btn_SendTo)
        edtMessage = view.findViewById(R.id.edt_message)

        btnAddSnap()
        btnSendTo()

        return view
    }

    private fun btnAddSnap(){
        btnAddSnap.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
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
            val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedImage)
            imgSelected.setImageBitmap(bitmap)
        }
    }

    private fun btnSendTo(){
        btnSendTo.setOnClickListener(View.OnClickListener {

            imgSelected.isDrawingCacheEnabled = true
            imgSelected.buildDrawingCache()
            val bitmap = (imgSelected.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val imageName = UUID.randomUUID().toString() + ".jpg"

            val storagePrefs = FirebaseStorage.getInstance().reference.child("images/snaps").child(imageName)
            val uploadTask = FirebaseStorage.getInstance().reference.child("images/snaps").child(imageName)
                .putBytes(data)

            uploadTask.addOnFailureListener {
                Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                storagePrefs.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it
                    val intent = Intent(activity,
                        ChooseUserActivity::class.java)
                    intent.putExtra("imageUrl",downloadUrl.toString())
                    intent.putExtra("imageName",imageName)
                    intent.putExtra("message",edtMessage.text.toString())
                    startActivity(intent)
                }
            }
        })
    }

}