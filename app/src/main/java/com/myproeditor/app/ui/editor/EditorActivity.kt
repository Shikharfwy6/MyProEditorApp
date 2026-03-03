package com.myproeditor.app.ui.editor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
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
    private val fileList = mutableListOf<MediaFile>() // Ab naya Data Class use kar rahe hain
    
    // Video Play karne ke liye
    private lateinit var videoPreview: VideoView
    private lateinit var tvPreviewText: TextView

    private val folderPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val documentFile = DocumentFile.fromTreeUri(this, uri)
            fileList.clear()
            
            documentFile?.listFiles()?.forEach { file ->
                if (file.type?.startsWith("video/") == true) {
                    fileList.add(MediaFile("🎬 " + (file.name ?: "Video"), file.uri, true))
                } else if (file.type?.startsWith("image/") == true) {
                    fileList.add(MediaFile("🖼️ " + (file.name ?: "Image"), file.uri, false))
                }
            }
            
            fileAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // UI Setup
        videoPreview = findViewById(R.id.video_preview)
        tvPreviewText = findViewById(R.id.tv_preview_text)
        
        val btnAddElement = findViewById<TextView>(R.id.btn_add_element)
        btnAddElement?.setOnClickListener {
            PlusMenuBottomSheet().show(supportFragmentManager, "PlusMenu")
        }

        // Folder Panel Setup aur Click Listener
        rvFiles = findViewById(R.id.rv_files)
        rvFiles.layoutManager = LinearLayoutManager(this)
        
        fileAdapter = FileBrowserAdapter(fileList) { clickedFile ->
            // Ye code tab chalega jab list me kisi file par click hoga
            if (clickedFile.isVideo) {
                tvPreviewText.visibility = View.GONE // "Select Video" wala text chhupa do
                videoPreview.setVideoURI(clickedFile.uri) // Video set karo
                videoPreview.start() // Video play karo!
                Toast.makeText(this, "Playing Video in Preview!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ye ek Image hai. Abhi sirf video play hoga.", Toast.LENGTH_SHORT).show()
            }
        }
        
        rvFiles.adapter = fileAdapter

        val btnOpenFolder = findViewById<Button>(R.id.btn_open_folder)
        btnOpenFolder.setOnClickListener {
            folderPickerLauncher.launch(null)
        }
    }
}
