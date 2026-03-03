package com.myproeditor.app.ui.editor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.myproeditor.app.R
import com.myproeditor.app.ui.bottomsheet.PlusMenuBottomSheet

class EditorActivity : AppCompatActivity() {

    // Folder select karne ka system
    private val folderPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(this, "Project Folder Imported Successfully!", Toast.LENGTH_LONG).show()
            // Future C++ Logic: Yahan hum is folder ke saare video/image files read karenge
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // UI IDs ko dhoondna
        val btnAddElement = findViewById<TextView>(R.id.btn_add_element)
        
        // Plus (+) Button Click Logic
        btnAddElement?.setOnClickListener {
            val bottomSheet = PlusMenuBottomSheet()
            bottomSheet.show(supportFragmentManager, "PlusMenu")
        }

        // Feature: Import Root Folder (Pura project folder open karna)
        // Note: XML me agar Folder wale view ka ID nahi hai, toh abhi hum right panel ko click pe trigger kar sakte hain.
        // Yahan par hum abhi user ko direct intent bhejenge temporary check ke liye.
        Toast.makeText(this, "Editor Opened. Ready for C++ Engine.", Toast.LENGTH_SHORT).show()
    }
    
    // Ye function aap apne "Folder" textview par click karke bula sakte hain
    fun openProjectFolder() {
        folderPickerLauncher.launch(null)
    }
}
