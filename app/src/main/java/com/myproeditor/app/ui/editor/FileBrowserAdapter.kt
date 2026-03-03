package com.myproeditor.app.ui.editor

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myproeditor.app.R

// Ek naya Data Class banaya jo File ka Naam aur uski Location (Uri) dono save karega
data class MediaFile(val name: String, val uri: Uri, val isVideo: Boolean)

class FileBrowserAdapter(
    private val files: List<MediaFile>, 
    private val onFileClick: (MediaFile) -> Unit // Click Listener
) : RecyclerView.Adapter<FileBrowserAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFileName: TextView = view.findViewById(R.id.tv_file_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.tvFileName.text = file.name
        
        // Jab user list me kisi video par click karega:
        holder.itemView.setOnClickListener {
            onFileClick(file)
        }
    }

    override fun getItemCount(): Int = files.size
}
