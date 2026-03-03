package com.myproeditor.app.ui.editor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myproeditor.app.R
import com.myproeditor.app.ui.bottomsheet.PlusMenuBottomSheet

class EditorActivity : AppCompatActivity() {

    private lateinit var rvFiles: RecyclerView
    private lateinit var fileAdapter: FileBrowserAdapter
    private val fileList = mutableListOf<String>()

    // Folder select karne ka Magic Engine
    private val folderPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            // App ko permission dena taaki future me bhi folder read kar sake
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            
            val documentFile = DocumentFile.fromTreeUri(this, uri)
            fileList.clear() // Purani list saaf karein
            
            // Folder ke andar saari files check karna
            documentFile?.listFiles()?.forEach { file ->
                // Sirf MP4 Videos aur Images ko filter karein
                if (file.type?.startsWith("video/") == true) {
                    fileList.add("🎬 " + (file.name ?: "Video File"))
                } else if (file.type?.startsWith("image/") == true) {
                    fileList.add("🖼️ " + (file.name ?: "Image File"))
                }
            }
            
            if (fileList.isEmpty()) {
                Toast.makeText(this, "Is folder me koi Video ya Image nahi hai!", Toast.LENGTH_LONG).show()
            } else {
                fileAdapter.notifyDataSetChanged() // List ko update karna
                Toast.makeText(this, "Success! ${fileList.size} files imported.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // Plus (+) Button Logic
        val btnAddElement = findViewById<TextView>(R.id.btn_add_element)
        btnAddElement?.setOnClickListener {
            val bottomSheet = PlusMenuBottomSheet()
            bottomSheet.show(supportFragmentManager, "PlusMenu")
        }

        // Folder Panel Setup
        rvFiles = findViewById(R.id.rv_files)
        rvFiles.layoutManager = LinearLayoutManager(this)
        fileAdapter = FileBrowserAdapter(fileList)
        rvFiles.adapter = fileAdapter

        // Import Folder Button Click
        val btnOpenFolder = findViewById<Button>(R.id.btn_open_folder)
        btnOpenFolder.setOnClickListener {
            folderPickerLauncher.launch(null) // File Manager Kholega
        }
    }
}
