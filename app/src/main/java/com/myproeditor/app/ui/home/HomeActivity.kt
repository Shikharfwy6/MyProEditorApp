package com.myproeditor.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.myproeditor.app.R
import com.myproeditor.app.ui.editor.EditorActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // UI se buttons ko link karna
        val btnNewProject = findViewById<LinearLayout>(R.id.btn_new_project)
        val btnCustomEfforts = findViewById<LinearLayout>(R.id.ll_custom_efforts)
        val btnAddPlugin = findViewById<LinearLayout>(R.id.ll_add_plugin)

        // "New" Button par click karne se EditorActivity khulega
        btnNewProject.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        // Baki buttons par abhi sirf ek message (Toast) aayega
        btnCustomEfforts.setOnClickListener {
            Toast.makeText(this, "Custom Efforts feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnAddPlugin.setOnClickListener {
            Toast.makeText(this, "Add Plugin feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}
