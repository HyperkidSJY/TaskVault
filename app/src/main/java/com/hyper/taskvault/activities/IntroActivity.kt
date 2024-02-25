package com.hyper.taskvault.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyper.taskvault.databinding.ActivityIntroBinding
import com.hyper.taskvault.firebase.FireStoreClass

class IntroActivity : BaseActivity() {
    private  lateinit var binding : ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        installSplashScreen()

        var currentUserId = FireStoreClass().getCurrentUserID()

        if(currentUserId.isNotEmpty()){
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnSignUpIntro.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnSignInIntro.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}