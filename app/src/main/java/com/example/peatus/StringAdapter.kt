package com.example.peatus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StringAdapter(private val items: List<String>) : RecyclerView.Adapter<StringAdapter.ViewHolder>() {

    // 1. ViewHolder для одного элемента списка
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.content)
    }

    // 2. Создание нового ViewHolder (вызывается при создании строки)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    // 3. Связывание данных с ViewHolder (вызывается для каждого элемента списка)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position] // Установить текст из массива
    }

    // 4. Количество элементов в списке
    override fun getItemCount(): Int = items.size
}
