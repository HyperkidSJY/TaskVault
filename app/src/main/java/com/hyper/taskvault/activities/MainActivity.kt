package com.hyper.taskvault.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.hyper.taskvault.R
import com.hyper.taskvault.adapters.BoardItemsAdapter
import com.hyper.taskvault.constants.Constants
import com.hyper.taskvault.databinding.ActivityMainBinding
import com.hyper.taskvault.databinding.AppBarMainBinding
import com.hyper.taskvault.firebase.FireStoreClass
import com.hyper.taskvault.models.Board
import com.hyper.taskvault.models.User

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        binding.navView.setNavigationItemSelectedListener(this)

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().loadUserData(this@MainActivity,true)

        binding.appBarMainContainer.fabCreateBoard.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressedDispatcher.onBackPressed()
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user: User,isToReadBoardsList: Boolean) {

        mUserName = user.name

        val headerView = binding.navView.getHeaderView(0)
        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)

        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        navUsername.text = user.name

        if (isToReadBoardsList) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardsList(this@MainActivity)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this@MainActivity, MyProfileActivity::class.java), MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.appBarMainContainer.toolbarMainActivity)
        binding.appBarMainContainer.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        binding.appBarMainContainer.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {

        hideProgressDialog()

        if (boardsList.size > 0) {
            binding.appBarMainContainer.mainContent.rvBoardsList.visibility = View.VISIBLE
            binding.appBarMainContainer.mainContent.tvNoBoardsAvailable.visibility = View.GONE

            binding.appBarMainContainer.mainContent.rvBoardsList.layoutManager = LinearLayoutManager(this@MainActivity)
            binding.appBarMainContainer.mainContent.rvBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this@MainActivity, boardsList)
            binding.appBarMainContainer.mainContent.rvBoardsList.adapter = adapter
        } else {
            binding.appBarMainContainer.mainContent.rvBoardsList.visibility = View.GONE
            binding.appBarMainContainer.mainContent.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            FireStoreClass().loadUserData(this@MainActivity)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }
}