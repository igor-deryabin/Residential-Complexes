package com.example.buildings.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewAdapterBase<M, VH: RecyclerView.ViewHolder>
    : RecyclerView.Adapter<VH> () {

    val models = ArrayList<M>()

    fun addAll(data: Collection<M>) {
        models.addAll(data)

        val addedSize = data.size
        val oldSize = models.size - addedSize

        notifyItemRangeChanged(oldSize, addedSize)
    }

    fun addAll(position: Int, data: Collection<M>) {
        models.addAll(position, data)

        val addedSize = data.size

        notifyItemRangeChanged(position, addedSize)
    }

    fun clearAndAddAll(data: Collection<M>) {
        models.clear()
        models.addAll(data)
        notifyDataSetChanged()
    }

    fun removeAllItems() {
        models.clear()
        notifyDataSetChanged()
    }

    fun moveItemRangeChanged(fromPosition: Int, toPosition: Int) {
        val item = models.removeAt(fromPosition)
        models.add(toPosition, item)
        notifyItemRangeChanged(toPosition, fromPosition + 1)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = models.removeAt(fromPosition)
        models.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun addItem(item: M) {
        models.add(item)
        notifyItemInserted(models.size - 1)
    }

    fun insertItem(position: Int, item: M) {
        models.add(position, item)
        notifyItemInserted(position)
    }

    fun insertItems(items: List<M>, position: Int) {
        models.addAll(position, items)
        notifyItemRangeInserted(position, items.size)
    }

    open fun removeItem(position: Int) {
        models.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun removeItemDataSetChanged(position: Int) {
        models.removeAt(position)
        notifyDataSetChanged()
    }

    fun replaceItem(position: Int, item: M) {
        models[position] = item
        notifyItemChanged(position)
    }

    fun replaceItems(position: Int, items: List<M>) {
        val count = items.size

        models.subList(position, (position + count).coerceAtMost(models.size)).clear()
        models.addAll(position, items)

        notifyItemRangeChanged(position, count)
    }

    fun removeItems(position: Int, count: Int) {
        models.subList(position, position + count).clear()
        notifyItemRangeRemoved(position, count)
    }

    protected abstract fun initViewHolder(holder: VH)

    protected abstract fun setModel(holder: VH, model: M)

    protected abstract fun createView(parent: ViewGroup, viewType: Int): VH

    fun getItem(position: Int): M = models[position]

    fun setItems(items: List<M>) {
        models.clear()
        models.addAll(items)
    }

    fun getItems(): List<M> = ArrayList(models)

    fun getItemsO(): List<M> = models

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return createView(parent, viewType).apply {
            initViewHolder(this)
        }
    }

    override fun getItemCount(): Int = models.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        setModel(holder, getItem(position))
    }
}