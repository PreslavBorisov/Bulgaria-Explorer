package com.bulgariaexplorer.app.ui.admin.pois

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import com.bulgariaexplorer.app.databinding.ItemAdminPoiBinding

class AdminPoiAdapter(
    private var pois: List<PoiResponse>,
    private val onPoiClick: (PoiResponse) -> Unit
) : RecyclerView.Adapter<AdminPoiAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAdminPoiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminPoiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val poi = pois[position]
        holder.binding.apply {
            tvPoiTitle.text = poi.title ?: "Без заглавие"
            tvPoiCity.text = poi.city ?: "Неизвестен град"
            chipActive.text = if (poi.active) "Активен" else "Неактивен"
            chipActive.setChipBackgroundColorResource(
                if (poi.active) android.R.color.holo_green_light
                else android.R.color.holo_red_light
            )
        }
        holder.itemView.setOnClickListener { onPoiClick(poi) }
    }

    override fun getItemCount() = pois.size

    fun updatePois(newPois: List<PoiResponse>) {
        pois = newPois
        notifyDataSetChanged()
    }
}
