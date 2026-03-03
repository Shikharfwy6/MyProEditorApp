package com.myproeditor.app.ui.editor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myproeditor.app.R
import com.myproeditor.app.ui.bottomsheet.PlusMenuBottomSheet

class EditorActivity : AppCompatActivity() {

    private lateinit var rvFiles: RecyclerView
    private lateinit var fileAdapter: FileBrowserAdapter
    private val fileList = mutableListOf<MediaFile>()
    
    private lateinit var videoPreview: VideoView
    private lateinit var tvPreviewText: TextView
    private lateinit var tvLayer2: TextView // Timeline Layer

    // Folder Selector Engine
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

        videoPreview = findViewById(R.id.video_preview)
        tvPreviewText = findViewById(R.id.tv_preview_text)
        tvLayer2 = findViewById(R.id.tv_layer_2) // Timeline layer initialize kiya

        // Plus Button
        findViewById<TextView>(R.id.btn_add_element).setOnClickListener {
            PlusMenuBottomSheet().show(supportFragmentManager, "PlusMenu")
        }

        // Keyboard & Mouse Buttons (Jo pehle gayab ho gaye the)
        findViewById<CardView>(R.id.btn_keyboard).setOnClickListener {
            Toast.makeText(this, "Keyboard mapping settings will open here.", Toast.LENGTH_SHORT).show()
        }
        findViewById<CardView>(R.id.btn_mouse).setOnClickListener {
            Toast.makeText(this, "Mouse settings will open here.", Toast.LENGTH_SHORT).show()
        }

        // Folder Panel Setup
        rvFiles = findViewById(R.id.rv_files)
        rvFiles.layoutManager = LinearLayoutManager(this)
        
        fileAdapter = FileBrowserAdapter(fileList) { clickedFile ->
            if (clickedFile.isVideo) {
                // 1. Play in Preview
                tvPreviewText.visibility = View.GONE
                videoPreview.setVideoURI(clickedFile.uri)
                videoPreview.start()
                
                // 2. Add to Timeline Layer! (Magic yahan hai)
                tvLayer2.text = clickedFile.name // Layer par video ka naam aa jayega
                Toast.makeText(this, "Video added to Layer 2", Toast.LENGTH_SHORT).show()
            }
        }
        rvFiles.adapter = fileAdapter

        findViewById<Button>(R.id.btn_open_folder).setOnClickListener {
            folderPickerLauncher.launch(null)
        }
    }

    // ==========================================
    // KEYBOARD SHORTCUT ENGINE (C = Cut, Space = Play/Pause)
    // ==========================================
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            // Agar Keyboard se 'C' dabaya
            KeyEvent.KEYCODE_C -> {
                Toast.makeText(this, "✂️ CUT applied at current frame!", Toast.LENGTH_LONG).show()
                // Yahan future me C++ function aayega jo video ko 2 hisso me baant dega
                return true
            }
            // Agar Spacebar dabaya (Play/Pause ke liye)
            KeyEvent.KEYCODE_SPACE -> {
                if (videoPreview.isPlaying) {
                    videoPreview.pause()
                    Toast.makeText(this, "⏸ Paused", Toast.LENGTH_SHORT).show()
                } else {
                    videoPreview.start()
                    Toast.makeText(this, "▶️ Playing", Toast.LENGTH_SHORT).show()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
