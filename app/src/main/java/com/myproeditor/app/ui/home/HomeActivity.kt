package com.myproeditor.app.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myproeditor.app.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ye aapka Image 1 wala UI load karega
        setContentView(R.layout.activity_home) 
    }
}