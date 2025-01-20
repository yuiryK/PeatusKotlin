package com.example.peatus

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ItemFragment : Fragment() {

    private var columnCount = 1
    private var stringList: List<String> = listOf() // Пустой список по умолчанию

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            stringList = it.getStringArrayList(ARG_STRING_LIST) ?: listOf() // Получаем список строк
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter(stringList)
            }
        }
        return view
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_STRING_LIST = "string-list"

       // @JvmStatic
        fun newInstance(columnCount: Int, stringList: List<String>) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putStringArrayList(ARG_STRING_LIST, ArrayList(stringList))
                }
            }
    }
}
