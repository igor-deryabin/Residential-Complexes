package com.example.buildings.presentation.fragments.complexes.adapterComplexes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.buildings.databinding.ItemComplexBinding
import com.example.buildings.domain.data.Complex
import com.example.buildings.utils.RecyclerViewAdapterBase

class ComplexesAdapter(
    private val context: Context,
    private val listener: ComplexesListener,
    items: List<Complex>
): RecyclerViewAdapterBase<Complex, ComplexVH>() {

    init {
        addAll(items)
    }

    override fun initViewHolder(holder: ComplexVH) {

    }

    override fun setModel(holder: ComplexVH, model: Complex) {
        holder.setData(model)
    }

    override fun createView(parent: ViewGroup, viewType: Int): ComplexVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemComplexBinding.inflate(inflater, parent, false)
        return ComplexVH(binding, context, listener)
    }
}