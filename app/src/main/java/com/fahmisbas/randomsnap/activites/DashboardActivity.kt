package com.fahmisbas.randomsnap.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fahmisbas.randomsnap.R
import com.fahmisbas.randomsnap.fragments.AddSnapFragment
import com.fahmisbas.randomsnap.fragments.InboxFragment
import com.fahmisbas.randomsnap.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            ProfileFragment()
        ).commit()
    }

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inbox -> {
                    toFragment(InboxFragment())
                }
                R.id.nav_addSnap -> {
                    toFragment(AddSnapFragment())
                }
                R.id.nav_profile -> {
                    toFragment(ProfileFragment())
                }
            }
            true
        }

    private fun toFragment(selectedFragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, selectedFragment)
            .commit()
    }

    override fun onBackPressed() {
        auth.signOut()
        super.onBackPressed()
    }
}
