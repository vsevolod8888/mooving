package com.example.mymoovingpicturedagger.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.databinding.RouteHolderNewBinding
import com.example.mymoovingpicturedagger.domain.RouteDomain
import com.google.android.material.button.MaterialButton

class Adapter(
    var routeListener: RouteListener
) : ListAdapter<RouteDomain, Adapter.RouteHolder>(SleepNightDiffCallback()) {
    var selectedItemPosition: Int = -1 // для чего это???
    var passportsAndNames: HashMap<Int, String> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolder {
        return RouteHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            routeListener.onClickK(item)
            selectItemPosition(position)
        }
        val btnDownload = holder.itemView.findViewById<MaterialButton>(R.id.buttonDownload)

        if (item.isClicked) {
            btnDownload.setBackgroundResource(R.drawable.ic_ready_downloaded)
        } else {
            btnDownload.setBackgroundResource(R.drawable.ic_upload_foreground)
        }
        btnDownload.setOnClickListener {
            routeListener.onUploadOnSErverClick(item)
            selectItemPosition(position)
        }
        holder.bind(
            item!!
        )
    }
    fun selectItemPosition(itemPos: Int) {
        selectedItemPosition = itemPos
        notifyDataSetChanged()
    }
    class RouteHolder(val binding: RouteHolderNewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: RouteDomain
        ) {
            binding.route = item
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): RouteHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RouteHolderNewBinding.inflate(layoutInflater, parent, false)
                return RouteHolder(binding)
            }
        }
    }
}
class SleepNightDiffCallback : DiffUtil.ItemCallback<RouteDomain>() {
    override fun areItemsTheSame(
        oldItem: RouteDomain,
        newItem: RouteDomain
    ): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: RouteDomain,
        newItem: RouteDomain
    ): Boolean {
        return oldItem == newItem
    }


}

interface RouteListener {
    fun onClickK(itemDetail: RouteDomain)
    fun onUploadOnSErverClick(itemDetail: RouteDomain)
}