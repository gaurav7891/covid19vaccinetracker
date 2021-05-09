package com.nativework.covid19vaccinetracker.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.nativework.covid19vaccinetracker.R
import java.util.*
import kotlin.collections.ArrayList


class AutocompleteAdapter(private val mContext: Context, private val resource: Int, private var dataList: List<String>) : ArrayAdapter<String>(mContext, resource), Filterable {

    private var listFilter = ListFilter()
    private var dataListAllItems: List<String>? = null

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): String {
        return dataList[position]
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, mView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(resource, parent, false)
        val title = view.findViewById<TextView>(R.id.txtTitle)
        title.text = getItem(position)
        return view
    }

    override fun getFilter(): Filter {
        return listFilter
    }

    inner class ListFilter : Filter() {
        private val lock = Any()

        override fun performFiltering(prefix: CharSequence?): FilterResults {
            val results = FilterResults()
            if (dataListAllItems == null) {
                synchronized(lock) { dataListAllItems = ArrayList<String>(dataList) }
            }

            if (prefix == null || prefix.isEmpty()) {
                synchronized(lock) {
                    results.values = dataListAllItems
                    results.count = dataListAllItems?.size!!
                }
            } else {
                val searchStrLowerCase: String = prefix.toString().toLowerCase(Locale.ROOT)
                val matchValues = ArrayList<String>()
                for (dataItem in dataListAllItems!!) {
                    if (dataItem.toLowerCase(Locale.ROOT).startsWith(searchStrLowerCase)) {
                        matchValues.add(dataItem)
                    }
                }
                results.values = matchValues
                results.count = matchValues.size
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            dataList = if (results?.values != null) {
                results.values as ArrayList<String>
            } else {
                emptyList()
            }
            if (results?.count!! > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
}