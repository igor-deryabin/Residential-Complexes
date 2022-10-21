package com.example.buildings.presentation.fragments.complexes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.buildings.R

class AreasAdapter(
    context: Context,
    list: List<String>
) : ArrayAdapter<String>(context, 0, list) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View =
            convertView ?: layoutInflater.inflate(R.layout.item_spinner_area, parent, false)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        getItem(position)?.let { tvName.text = it }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = layoutInflater.inflate(R.layout.item_area, parent, false)

        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val ivArrow = view.findViewById<ImageView>(R.id.iv_arrow)
        if (position == 0) {
            ivArrow.visibility = View.VISIBLE
        } else {
            ivArrow.visibility = View.GONE
        }

        getItem(position)?.let { tvName.text = it }

        return view
    }
}