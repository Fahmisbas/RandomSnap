package com.fahmisbas.randomsnap.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.fahmisbas.randomsnap.R
import com.fahmisbas.randomsnap.User
import com.fahmisbas.randomsnap.UserListAdapter
import com.fahmisbas.randomsnap.activites.ViewSnapActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class InboxFragment : Fragment() {

    private val snaps : ArrayList<DataSnapshot> = ArrayList<DataSnapshot>()
    private var userList : ArrayList<User> = ArrayList<User>()
    lateinit var adapter: UserListAdapter
    lateinit var rvUserInbox : RecyclerView
    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_inbox,container,false)

        rvUserInbox = view.findViewById(R.id.rv_userInbox)
        onItemClickListenet()


        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("snaps")
            .addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val from = p0.child("from").value as String
                    val imageUrl = p0.child("imageUrl").value as String
                    snaps.add(p0)

                    val user = User()
                    user.userName = from
                    user.imageurl = imageUrl
                    userList.add(user)


                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(p0: DatabaseError) {}
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                override fun onChildRemoved(p0: DataSnapshot) {
                    var index = 0
                    for (snap : DataSnapshot in snaps) {
                        if (snap.key == p0.key) {
                            snaps.removeAt(index)
                            userList.removeAt(index)
                        }
                        index++
                    }
                    adapter.notifyDataSetChanged()
                }
            })

        return view
    }

    private fun onItemClickListenet() {
        adapter = UserListAdapter(userList,
            object : UserListAdapter.OnItemClick {
                override fun onItemClick(position: Int) {
                    val snapshot = snaps[position]

                    val intent = Intent(
                        activity,
                        ViewSnapActivity::class.java
                    )
                    intent.putExtra("imageName", snapshot.child("imageName").value as String)
                    intent.putExtra("imageUrl", snapshot.child("imageUrl").value as String)
                    intent.putExtra("message", snapshot.child("message").value as String)
                    intent.putExtra("snapKey", snapshot.key)

                    startActivity(intent)
                }
            })

        rvUserInbox.adapter = adapter
    }
}