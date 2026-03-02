package com.myproeditor.app.ui.editor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.myproeditor.app.R

class EditorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ye aapka Image 2 wala UI load karega
        setContentView(R.layout.activity_editor)
    }
}