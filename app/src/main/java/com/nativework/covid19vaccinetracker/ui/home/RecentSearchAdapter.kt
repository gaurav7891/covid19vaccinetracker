package com.nativework.covid19vaccinetracker.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.models.SavedPreferences
import com.nativework.covid19vaccinetracker.utils.Constants

class RecentSearchAdapter(
    private val mContext: Context,
    private val list: ArrayList<SavedPreferences>,
    private val listener: OnClickListener
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

        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtSubTitle: TextView = itemView.findViewById(R.id.txtSubTitle)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        fun bind(center: SavedPreferences?) {
            if (center?.type == Constants.SEARCH_BY_DISTRICT) {
                txtSubTitle.text = center.district
                txtTitle.text = center.state
            }
            if (center?.type == Constants.SEARCH_BY_PINCODE) {
                txtTitle.text = center.pinCode
                txtSubTitle.text = "PINCODE"
            }
            cardView.setOnClickListener {
                listener.onImageClick(center)
            }
        }
    }

    interface OnClickListener {
        fun onImageClick(center: SavedPreferences?)
    }
}