package com.nativework.covid19vaccinetracker.ui.center

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.models.Session

class SlotAdapter(private val mContext: Context, private val list: ArrayList<Session>) :
RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout_sessions, parent, false)
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

        private var txtVaccineBrand: TextView = itemView.findViewById(R.id.txtVaccineBrand)
        private var txtAgeLimit: TextView = itemView.findViewById(R.id.txtAgeLimit)
        private var txtAvailableCapacity: TextView =
            itemView.findViewById(R.id.txtAvailableCapacity)
        private var txtDoes1AvailableCapacity: TextView =
            itemView.findViewById(R.id.txtDoes1AvailableCapacity)
        private var txtDoes2AvailableCapacity: TextView =
            itemView.findViewById(R.id.txtDoes2AvailableCapacity)

        private var sl1: TextView = itemView.findViewById(R.id.sl1)
        private var sl2: TextView = itemView.findViewById(R.id.sl2)
        private var sl3: TextView = itemView.findViewById(R.id.sl3)
        private var sl4: TextView = itemView.findViewById(R.id.sl4)

        fun bind(session: Session) {
            txtAgeLimit.text = "Age Limit: ${session.min_age_limit}"
            txtAvailableCapacity.text = "${session.available_capacity}"
            txtDoes1AvailableCapacity.text = "${session.available_capacity_dose1}"
            txtDoes2AvailableCapacity.text = "${session.available_capacity_dose2}"

            txtVaccineBrand.text = session.vaccine
            val slots = session.slots
            val slotsDetail = StringBuffer()
            if (slots?.size!! > 0){
                for (s in slots){
                    slotsDetail.append(" $s")
                }
                sl1.text = slotsDetail.toString()
            }else{
                sl1.text = "No slots available"
            }
           /* sl1.text = slots?.get(0)
            sl2.text = slots?.get(1)
            sl3.text = slots?.get(2)
            sl4.text = slots?.get(3)*/
        }


    }
}