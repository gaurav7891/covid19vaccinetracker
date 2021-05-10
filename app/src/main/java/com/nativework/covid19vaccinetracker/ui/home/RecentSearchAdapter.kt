package com.nativework.covid19vaccinetracker.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.models.SavedPreferences

class RecentSearchAdapter(
    private val mContext: Context,
    private val list: ArrayList<SavedPreferences>,
    private val listener:OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout_recent_search, parent, false)
        return RecentSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecentSearchViewHolder) {
            holder.bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class RecentSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txtState: TextView = itemView.findViewById(R.id.txtState)
        private val txtDistrict: TextView = itemView.findViewById(R.id.txtDistrict)
        private val imgForward: ImageView = itemView.findViewById(R.id.imgForward)

        fun bind(center: SavedPreferences) {
            txtDistrict.text = center.district
            txtState.text = center.state
            imgForward.setOnClickListener {
                listener.onImageClick(center)
            }
        }
    }

    interface OnClickListener{
        fun onImageClick(center: SavedPreferences)
    }
}