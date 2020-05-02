package com.fahmisbas.randomsnap.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.fahmisbas.randomsnap.R
import com.fahmisbas.randomsnap.User
import com.fahmisbas.randomsnap.UserListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChooseUserActivity : AppCompatActivity() {

    private lateinit var rvUserList: RecyclerView
    private  var userList: ArrayList<User> = ArrayList<User>()
    private lateinit var adapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        onClickItemListener()

        FirebaseDatabase.getInstance().reference.child("users")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val username = p0.child("username").value as String
                    val keys = p0.key!!
                    val profilePicUrl = p0.child("profilePicUrl").value as String

                    val user = User()
                    user.imageurl = profilePicUrl
                    user.keys = keys
                    user.userName = username

                    userList.add(user)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(p0: DatabaseError) {}
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                override fun onChildRemoved(p0: DataSnapshot) {}

            })
    }

    private fun onClickItemListener() {
        rvUserList = findViewById(R.id.rv_chooseuser)
        adapter = UserListAdapter(
            userList,
            object : UserListAdapter.OnItemClick {
                override fun onItemClick(position: Int) {
                    val snapMap: Map<String, String> = mapOf(
                        "from" to FirebaseAuth.getInstance().currentUser!!.email!!,
                        "imageUrl" to intent.getStringExtra("imageUrl"),
                        "imageName" to intent.getStringExtra("imageName"),
                        "message" to intent.getStringExtra("message")
                    )

                    val user = userList[position]

                    FirebaseDatabase.getInstance().getReference("users").child(user.keys)
                        .child("snaps").push().setValue(snapMap)

                    val intent = Intent(
                        this@ChooseUserActivity,
                        DashboardActivity::class.java
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            })

        rvUserList.adapter = adapter

    }

}
