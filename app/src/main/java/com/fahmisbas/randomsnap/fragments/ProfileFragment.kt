package com.fahmisbas.randomsnap.fragments

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fahmisbas.randomsnap.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment : Fragment() {

    lateinit var tvUsername: TextView
    lateinit var edtUsername: EditText
    lateinit var tvDesc: TextView
    lateinit var edtDesc: EditText
    lateinit var imageProfilePicture: CircleImageView;

    lateinit var btnEditProfileInfo: Button
    lateinit var btnApplyEditUserInfo : Button

    var image : String = ""

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvUsername = view.findViewById(R.id.tv_username)
        tvDesc = view.findViewById(R.id.tv_desc)
        edtUsername = view.findViewById(R.id.edt_username)
        edtDesc = view.findViewById(R.id.edt_desc)
        imageProfilePicture = view.findViewById(R.id.img_profile_picture)

        btnEditProfileInfo = view.findViewById(R.id.btn_edit_userinfo)
        btnApplyEditUserInfo  = view.findViewById(R.id.btn_applyedit)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getImageUrl()
        getUserName()
        setTextUserName()
    }

    private fun getUserName() {
        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    val username = p0.child("username").value as String
                    tvUsername.text = username
                }
            })
    }

    lateinit var username : String
    lateinit var desc : String
    private fun setTextUserName() {
        btnEditProfileInfo.setOnClickListener(View.OnClickListener {
            edtUsername.visibility = View.VISIBLE
            edtDesc.visibility = View.VISIBLE
            btnApplyEditUserInfo.visibility = View.VISIBLE
            tvUsername.visibility = View.INVISIBLE
            tvDesc.visibility = View.INVISIBLE

            edtUsername.setText(tvUsername.text.toString())
            edtDesc.setText(tvDesc.text.toString())

        })

        btnApplyEditUserInfo.setOnClickListener(View.OnClickListener {

            if (edtUsername.text.toString().isNotEmpty()) {
                username = edtUsername.text.toString()
            }else if (edtDesc.text.toString().isNotEmpty()){
                desc = edtDesc.text.toString()
            }

            val setChildValue = FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid)
                setChildValue.child("username").setValue(username)
                setChildValue.child("description").setValue(desc)

            edtUsername.visibility = View.INVISIBLE
            edtDesc.visibility = View.INVISIBLE
            btnApplyEditUserInfo.visibility = View.INVISIBLE

            tvUsername.visibility = View.VISIBLE
            tvDesc.visibility = View.VISIBLE

            getUserName()
        })
    }

    private fun getImageUrl() {
        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    val imageUrl = p0.child("profilePicUrl").value as String
                    setProfilePic(imageUrl)
                }
            })
    }

    private fun setProfilePic(imageUrl: String) {
        if (activity == null) {
            return
        }
        Glide.with(this)
            .load(imageUrl)
            .into(imageProfilePicture)
    }
}