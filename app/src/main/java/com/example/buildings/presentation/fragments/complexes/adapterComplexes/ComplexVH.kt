package com.example.buildings.presentation.fragments.complexes.adapterComplexes

import android.content.Context
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buildings.databinding.ItemComplexBinding
import com.example.buildings.domain.data.Complex
import java.io.File

open class ComplexVH(
    private val binding: ItemComplexBinding,
    private val context: Context,
    private val listener: ComplexesListener
): RecyclerView.ViewHolder(binding.root) {

    fun setData(item: Complex) {
        binding.apply {
            tvName.text = item.name
            tvArea.text = item.area

            if (item.image.isNotEmpty()) {
                ivMain.setImageURI(item.image.toUri())
                Glide.with(context)
                    .load(File(item.image))
                    .into(ivMain)
            }

            clItem.setOnClickListener { listener.openComplex(item) }
        }
    }
}