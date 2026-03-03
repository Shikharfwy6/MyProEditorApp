package com.myproeditor.app.ui.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myproeditor.app.R

class FileBrowserAdapter(private val files: List<String>) : RecyclerView.Adapter<FileBrowserAdapter.FileViewHolder>() {

    class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFileName: TextView = view.findViewById(R.id.tv_file_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.tvFileName.text = files[position]
    }

    override fun getItemCount(): Int = files.size
}
