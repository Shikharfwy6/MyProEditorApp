package com.myproeditor.app.ui.editor

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.myproeditor.app.R
import com.myproeditor.app.ui.bottomsheet.PlusMenuBottomSheet

class EditorActivity : AppCompatActivity() {

    private lateinit var rvFiles: RecyclerView
    private lateinit var fileAdapter: FileBrowserAdapter
    private val fileList = mutableListOf<MediaFile>()
    
    private lateinit var videoPreview: VideoView
    private lateinit var tvPreviewText: TextView
    private lateinit var trackVideo: LinearLayout
    
    // Mouse Variables
    private lateinit var mousePointer: TextView
    private lateinit var mouseTrackpad: FrameLayout
    private var isMouseEnabled = false
    private var lastX = 0f
    private var lastY = 0f

    private val folderPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val documentFile = DocumentFile.fromTreeUri(this, uri)
            fileList.clear()
            documentFile?.listFiles()?.forEach { file ->
                if (file.type?.startsWith("video/") == true) {
                    fileList.add(MediaFile("🎬 " + (file.name ?: "Video"), file.uri, true))
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
        trackVideo = findViewById(R.id.track_video)
        
        mousePointer = findViewById(R.id.mouse_pointer)
        mouseTrackpad = findViewById(R.id.mouse_trackpad)

        // 1. DYNAMIC TIMELINE SYSTEM
        rvFiles = findViewById(R.id.rv_files)
        rvFiles.layoutManager = LinearLayoutManager(this)
        fileAdapter = FileBrowserAdapter(fileList) { clickedFile ->
            if (clickedFile.isVideo) {
                tvPreviewText.visibility = View.GONE
                videoPreview.setVideoURI(clickedFile.uri)
                videoPreview.start()
                
                // Add clip to timeline
                val clipView = TextView(this)
                clipView.text = clickedFile.name
                clipView.setTextColor(Color.WHITE)
                clipView.setBackgroundColor(Color.parseColor("#F44336")) // Red color
                clipView.setPadding(20, 10, 20, 10)
                clipView.textSize = 10f
                
                val params = LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.MATCH_PARENT)
                params.setMargins(0, 0, 5, 0)
                clipView.layoutParams = params
                
                clipView.setOnClickListener {
                    Toast.makeText(this, "Layer Selected: ${clickedFile.name}", Toast.LENGTH_SHORT).show()
                }
                
                trackVideo.addView(clipView)
            }
        }
        rvFiles.adapter = fileAdapter

        // UI Buttons ko TextView me convert kiya
        findViewById<TextView>(R.id.btn_open_folder).setOnClickListener { folderPickerLauncher.launch(null) }
        findViewById<TextView>(R.id.btn_add_element).setOnClickListener { PlusMenuBottomSheet().show(supportFragmentManager, "PlusMenu") }

        // 2. VIRTUAL MOUSE TRACKPAD SYSTEM
        val btnMouse = findViewById<TextView>(R.id.btn_mouse)
        
        btnMouse.setOnClickListener {
            isMouseEnabled = !isMouseEnabled
            if (isMouseEnabled) {
                mouseTrackpad.visibility = View.VISIBLE
                mousePointer.visibility = View.VISIBLE
                btnMouse.text = "🖱 Disable Mouse"
            } else {
                mouseTrackpad.visibility = View.GONE
                mousePointer.visibility = View.GONE
                btnMouse.text = "🖱 Enable Mouse"
            }
        }

        mouseTrackpad.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastX
                    val dy = event.y - lastY
                    mousePointer.x += dx * 1.5f
                    mousePointer.y += dy * 1.5f
                    lastX = event.x
                    lastY = event.y
                }
            }
            true
        }

        // 3. 108-KEY VIRTUAL KEYBOARD SYSTEM
        findViewById<TextView>(R.id.btn_keyboard).setOnClickListener {
            showVirtualKeyboard()
        }
    }

    private fun showVirtualKeyboard() {
        val dialog = BottomSheetDialog(this)
        val scrollView = ScrollView(this)
        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.setPadding(10, 10, 10, 10)
        mainLayout.setBackgroundColor(Color.parseColor("#121212"))

        val keys = listOf(
            listOf("Esc", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"),
            listOf("~", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "Backspace"),
            listOf("Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\"),
            listOf("Caps", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "Enter"),
            listOf("Shift", "Z", "X", "C (Cut)", "V", "B", "N", "M", ",", ".", "/", "Shift"),
            listOf("Ctrl", "Win", "Alt", "Space (Play/Pause)", "Alt", "Ctrl", "◄", "▼", "▲", "►")
        )

        val title = TextView(this)
        title.text = "108-Key Virtual Keyboard"
        title.setTextColor(Color.WHITE)
        title.textSize = 18f
        title.setPadding(0,0,0,20)
        mainLayout.addView(title)

        for (row in keys) {
            val rowLayout = LinearLayout(this)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.gravity = Gravity.CENTER
            
            for (key in row) {
                val btn = Button(this)
                btn.text = key
                btn.textSize = 10f
                btn.setPadding(5,5,5,5)
                
                btn.setOnClickListener {
                    when (key) {
                        "C (Cut)" -> Toast.makeText(this, "✂️ CUT Applied at Timeline!", Toast.LENGTH_LONG).show()
                        "Space (Play/Pause)" -> {
                            if (videoPreview.isPlaying) videoPreview.pause() else videoPreview.start()
                        }
                        else -> Toast.makeText(this, "Key Pressed: $key", Toast.LENGTH_SHORT).show()
                    }
                }
                
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.setMargins(2, 2, 2, 2)
                rowLayout.addView(btn, params)
            }
            mainLayout.addView(rowLayout)
        }

        val hScroll = HorizontalScrollView(this)
        hScroll.addView(mainLayout)
        scrollView.addView(hScroll)
        
        dialog.setContentView(scrollView)
        dialog.show()
    }
}
