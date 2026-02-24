package com.azizlator.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.azizlator.R
import com.azizlator.engine.Container

class ContainerAdapter(
    private val containers: List<Container>,
    private val onClick: (Container) -> Unit
) : RecyclerView.Adapter<ContainerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card_container)
        val name: TextView = view.findViewById(R.id.tv_container_name)
        val status: TextView = view.findViewById(R.id.tv_container_status_item)
        val wine: TextView = view.findViewById(R.id.tv_wine_version)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val container = containers[position]
        holder.name.text = container.name
        holder.status.text = when (container.status) {
            "Running" -> "🟢 Running"
            "Ready" -> "⚫ Ready"
            else -> "⚫ ${container.status}"
        }
        holder.wine.text = container.wineVersion
        holder.card.setOnClickListener { onClick(container) }
    }

    override fun getItemCount() = containers.size
}
