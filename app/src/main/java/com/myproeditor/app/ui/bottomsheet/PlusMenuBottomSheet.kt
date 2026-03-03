package com.myproeditor.app.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.myproeditor.app.R

class PlusMenuBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_plus, container, false)

        view.findViewById<Button>(R.id.btn_3d_camera).setOnClickListener {
            Toast.makeText(context, "3D Camera Added to Timeline!", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        
        view.findViewById<Button>(R.id.btn_stock_api).setOnClickListener {
            Toast.makeText(context, "Opening Stock Footage API...", Toast.LENGTH_SHORT).show()
            // Future me yahan Pexels/Pixabay API ka page khulega
            dismiss()
        }

        return view
    }
}
