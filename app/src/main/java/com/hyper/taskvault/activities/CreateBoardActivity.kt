package com.hyper.taskvault.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hyper.taskvault.R
import com.hyper.taskvault.constants.Constants
import com.hyper.taskvault.databinding.ActivityCreateBoardBinding
import com.hyper.taskvault.firebase.FireStoreClass
import com.hyper.taskvault.models.Board

class CreateBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateBoardBinding

    private var mSelectedImageFileUri: Uri? = null

    private lateinit var mUserName: String

    private var mBoardImageURL: String = ""

    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        mSelectedImageFileUri = it
        Glide
            .with(this@CreateBoardActivity)
            .load(it)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivBoardImage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        binding.ivBoardImage.setOnClickListener {
            imageContract.launch("image/*")
        }

        binding.btnCreate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadBoardImage()
            } else {

                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    private fun uploadBoardImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "BOARD_IMAGE" + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(this@CreateBoardActivity, mSelectedImageFileUri)
        )
        sRef.putFile(mSelectedImageFileUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        mBoardImageURL = uri.toString()
                        createBoard()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@CreateBoardActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }
    }

    private fun createBoard() {
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID()) // adding the current user id.
        val board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )
        FireStoreClass().createBoard(this@CreateBoardActivity, board)
    }


    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCreateBoardActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }

        binding.toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    fun boardCreatedSuccessfully() {

        hideProgressDialog()

        finish()
    }
}