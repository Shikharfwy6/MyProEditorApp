package com.myproeditor.app.ui.editor

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myproeditor.app.R
import com.myproeditor.app.ui.bottomsheet.PlusMenuBottomSheet

class EditorActivity : AppCompatActivity() {

    private lateinit var fileList: MutableList<MediaFile>
    private lateinit var fileAdapter: FileBrowserAdapter
    private lateinit var videoPreview: VideoView
    private lateinit var tvPreviewText: TextView
    private lateinit var trackVideo: LinearLayout
    
    // Mouse System
    private lateinit var mousePointer: ImageView
    private lateinit var mouseTrackpad: FrameLayout
    private var lastX = 0f
    private var lastY = 0f
    private var pointerX = 500f
    private var pointerY = 500f

    private val folderPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val documentFile = DocumentFile.fromTreeUri(this, uri)
            fileList.clear()
            documentFile?.listFiles()?.forEach { file ->
                if (file.type?.startsWith("video/") == true) fileList.add(MediaFile(file.name ?: "Video", file.uri, true))
                else if (file.type?.startsWith("image/") == true) fileList.add(MediaFile(file.name ?: "Image", file.uri, false))
            }
            fileAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        fileList = mutableListOf()
        videoPreview = findViewById(R.id.video_preview)
        tvPreviewText = findViewById(R.id.tv_preview_text)
        trackVideo = findViewById(R.id.track_video)
        mousePointer = findViewById(R.id.mouse_pointer)
        mouseTrackpad = findViewById(R.id.mouse_trackpad)

        val rvFiles = findViewById<RecyclerView>(R.id.rv_files)
        rvFiles.layoutManager = LinearLayoutManager(this)
        fileAdapter = FileBrowserAdapter(fileList) { clickedFile ->
            if (clickedFile.isVideo) {
                tvPreviewText.visibility = View.GONE
                videoPreview.setVideoURI(clickedFile.uri)
                videoPreview.start()
                
                val clipView = TextView(this)
                clipView.text = clickedFile.name
                clipView.setTextColor(Color.WHITE)
                clipView.setBackgroundColor(Color.parseColor("#F44336"))
                clipView.setPadding(15, 5, 15, 5)
                clipView.textSize = 8f
                val params = LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT)
                params.setMargins(0, 0, 5, 0)
                clipView.layoutParams = params
                trackVideo.addView(clipView)
            }
        }
        rvFiles.adapter = fileAdapter

        findViewById<TextView>(R.id.btn_open_folder).setOnClickListener { folderPickerLauncher.launch(null) }
        findViewById<ImageView>(R.id.btn_add_element).setOnClickListener { PlusMenuBottomSheet().show(supportFragmentManager, "PlusMenu") }

        // MOUSE TOUCHPAD SYSTEM
        mousePointer.x = pointerX
        mousePointer.y = pointerY
        mouseTrackpad.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> { lastX = event.x; lastY = event.y }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastX
                    val dy = event.y - lastY
                    pointerX += dx * 1.5f
                    pointerY += dy * 1.5f
                    mousePointer.x = pointerX
                    mousePointer.y = pointerY
                    lastX = event.x
                    lastY = event.y
                }
            }
            true
        }
        
        // LEFT & RIGHT CLICK BUTTONS
        findViewById<TextView>(R.id.btn_left_click).setOnClickListener {
            Toast.makeText(this, "Left Click at X:${pointerX.toInt()}", Toast.LENGTH_SHORT).show()
        }
        findViewById<TextView>(R.id.btn_right_click).setOnClickListener {
            Toast.makeText(this, "Right Click! Context Menu opening...", Toast.LENGTH_SHORT).show()
        }

        // BUILD PERMANENT KEYBOARD
        buildPermanentKeyboard()
    }

    private fun buildPermanentKeyboard() {
        val container = findViewById<LinearLayout>(R.id.keyboard_container)
        val numpadContainer = findViewById<LinearLayout>(R.id.numpad_container)
        
        // Main QWERTY Rows
        val keys = listOf(
            listOf("Esc", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"),
            listOf("~", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "Back"),
            listOf("Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\"),
            listOf("Caps", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "Enter"),
            listOf("Shift", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "/", "Shift"),
            listOf("Ctrl", "Win", "Alt", "Space", "Alt", "Fn", "Ctrl")
        )

        for (row in keys) {
            val rowLayout = LinearLayout(this)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            val rowParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            rowLayout.layoutParams = rowParams
            
            for (key in row) {
                val btn = TextView(this)
                btn.text = key
                btn.textSize = 6f // Very small to fit
                btn.setTextColor(Color.WHITE)
                btn.setBackgroundColor(Color.parseColor("#333333"))
                btn.gravity = Gravity.CENTER
                
                val paramWeight = if (key == "Space") 5f else if (key == "Enter" || key == "Shift") 2f else 1f
                val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, paramWeight)
                params.setMargins(1, 1, 1, 1)
                btn.layoutParams = params
                
                btn.setOnClickListener {
                    if (key == "C") Toast.makeText(this, "Cut Action!", Toast.LENGTH_SHORT).show()
                    if (key == "Space") { if (videoPreview.isPlaying) videoPreview.pause() else videoPreview.start() }
                }
                rowLayout.addView(btn)
            }
            container.addView(rowLayout)
        }

        // Numpad Rows
        val numKeys = listOf(
            listOf("Num", "/", "*", "-"),
            listOf("7", "8", "9", "+"),
            listOf("4", "5", "6", ""),
            listOf("1", "2", "3", "Ent"),
            listOf("0", ".", "", "")
        )
        for (row in numKeys) {
            val rowLayout = LinearLayout(this)
            rowLayout.orientation = LinearLayout.HORIZONTAL
            val rowParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            rowLayout.layoutParams = rowParams
            
            for (key in row) {
                if(key == "") continue
                val btn = TextView(this)
                btn.text = key
                btn.textSize = 6f
                btn.setTextColor(Color.WHITE)
                btn.setBackgroundColor(Color.parseColor("#333333"))
                btn.gravity = Gravity.CENTER
                
                val paramWeight = if (key == "0") 2f else 1f
                val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, paramWeight)
                params.setMargins(1, 1, 1, 1)
                btn.layoutParams = params
                rowLayout.addView(btn)
            }
            numpadContainer.addView(rowLayout)
        }
    }
}
