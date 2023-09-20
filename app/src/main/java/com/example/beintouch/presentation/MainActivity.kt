package com.example.beintouch.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.beintouch.R
import com.example.beintouch.fragments.Login

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, Login.newInstance())
            .commit()
    }
}
