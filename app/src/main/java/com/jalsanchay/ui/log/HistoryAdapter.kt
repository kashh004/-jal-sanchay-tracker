package com.jalsanchay.ui.log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jalsanchay.data.models.RainfallEntry
import com.jalsanchay.databinding.ItemHistoryBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(
    private val onDelete: (RainfallEntry) -> Unit
) : ListAdapter<RainfallEntry, HistoryAdapter.ViewHolder>(DIFF) {

    private val df = DecimalFormat("#,##0.#")
    private val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class ViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = getItem(position)
        with(holder.binding) {
            val parsed = runCatching { inFmt.parse(entry.date) }.getOrNull()
            tvDate.text = if (parsed != null) outFmt.format(parsed) else entry.date
            tvMeta.text = "${entry.rainfallMm}mm · ${entry.roofAreaM2}m² · coeff ${entry.runoffCoefficient}"
            tvLiters.text = "${df.format(entry.litersHarvested)} L"
            tvDays.text = "${df.format(entry.litersHarvested / 200.0)} days"
            btnDelete.setOnClickListener { onDelete(entry) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<RainfallEntry>() {
            override fun areItemsTheSame(a: RainfallEntry, b: RainfallEntry) = a.id == b.id
            override fun areContentsTheSame(a: RainfallEntry, b: RainfallEntry) = a == b
        }
    }
}
