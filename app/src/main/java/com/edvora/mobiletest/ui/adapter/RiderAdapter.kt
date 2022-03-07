package com.edvora.mobiletest.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.edvora.mobiletest.R
import com.edvora.mobiletest.databinding.ItemRideBinding
import com.edvora.mobiletest.model.Ride
import com.edvora.mobiletest.utils.fetchAndDisplayImage
import java.text.SimpleDateFormat
import java.util.*

class RiderAdapter : ListAdapter<Ride, RiderAdapter.ViewHolder>(diffUtil) {
    companion object{
        val diffUtil = object: DiffUtil.ItemCallback<Ride>(){
            override fun areItemsTheSame(oldItem: Ride, newItem: Ride) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Ride, newItem: Ride) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.item_ride, parent, false).run {
            ViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val ride = currentList[position]

        holder.binding.apply {

            textStateName.text = ride.state
            textCityName.text = ride.city

            textRideId.text = context.getString(R.string.ride_id, ride.id)
            textOriginStation.text = context.getString(R.string.origin_station, ride.originStationCode)
            textStationPath.text = context.getString(R.string.station_path, ride.stationPath.joinToString{ it.toString() })

            textDistance.text = context.getString(R.string.distance, ride.distance)

            val date = Date(ride.timestamp)
            val format = SimpleDateFormat("dd'${getDateOrdinalPosition(date)}' MMM yyyy H:m")
            textDate.text = context.getString(R.string.date, format.format(date))

            imageMap.fetchAndDisplayImage(ride.mapUrl)
        }
    }

    private fun getDateOrdinalPosition(date: Date): String{
        val calendar = Calendar.getInstance()
        calendar.time = date

        val day = calendar.get(Calendar.DATE)

        return if(day in 11..13) "th"
        else {
            when (day % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
    }

    override fun getItemCount() = currentList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = ItemRideBinding.bind(itemView)
    }
}