package com.nativework.covid19vaccinetracker.ui.center

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.models.Center
import com.nativework.covid19vaccinetracker.models.Session

class CenterAdapter(
    private val mContext: Context,
    private val list: ArrayList<Center>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var adapter: SlotAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout_center, parent, false)
        return CenterViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CenterViewHolder) {
            holder.bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class CenterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var txtCenterName: TextView = itemView.findViewById(R.id.txtCenterName)
        private var txtCenterAddress: TextView = itemView.findViewById(R.id.txtCenterAddress)
        private var txVaccinationType: TextView = itemView.findViewById(R.id.txVaccinationType)
        private var txtPinCode: TextView = itemView.findViewById(R.id.txtPinCode)
        private var slotRecyclerView: RecyclerView = itemView.findViewById(R.id.slotRecyclerView)
        private var imgNotification: ImageView = itemView.findViewById(R.id.imgNotification)


        fun bind(center: Center) {
            txtCenterName.text = center.name
            txtCenterAddress.text = center.address
            txVaccinationType.text = center.fee_type
            txtPinCode.text = center.pincode.toString()
            adapter = SlotAdapter(mContext, center.sessions as ArrayList<Session>)
            slotRecyclerView.layoutManager = LinearLayoutManager(mContext)
            slotRecyclerView.adapter = adapter
            imgNotification.tag = "Notification not active"
        }
    }
}