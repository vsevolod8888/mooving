package com.example.mymoovingpicturedagger.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.databinding.ArchiveHolderBinding
import com.example.mymoovingpicturedagger.domain.SendRouteDomain
import com.google.android.material.button.MaterialButton


class AdapterArchive(
    var routeListener: RouteArchiveListener
) :
    ListAdapter<SendRouteDomain, AdapterArchive.RouteHolderArchive>(ArchiveDiffCallback()) {
    var selectedItemPosition: Int = -1                                            // для чего это???


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolderArchive {
        return RouteHolderArchive.from(parent)
    }

    override fun onBindViewHolder(holder: RouteHolderArchive, position: Int) {
        val item = getItem(position)
        holder.itemView.findViewById<MaterialButton>(R.id.btnDelArchiveRoute).setOnClickListener {
            routeListener.onDeleteClickK(item)
            selectItemPosition(position)
        }
        holder.itemView.findViewById<MaterialButton>(R.id.btnDownloadArchiveRoute).setOnClickListener {
            routeListener.onDownloadClick(item)
            selectItemPosition(position)
        }
        holder.bind(item)
    }

    fun selectItemPosition(itemPos: Int) {
        selectedItemPosition = itemPos
        notifyDataSetChanged()
    }

    class RouteHolderArchive(val binding: ArchiveHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: SendRouteDomain
        ) {
            // var button:MaterialButton = itemView.findViewById(R.id.buttonDownload)
            binding.routeServer = item
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): RouteHolderArchive {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ArchiveHolderBinding.inflate(layoutInflater, parent, false)
                return RouteHolderArchive(binding)
            }
        }
    }
}

class ArchiveDiffCallback : DiffUtil.ItemCallback<SendRouteDomain>() {
    override fun areItemsTheSame(
        oldItem: SendRouteDomain,
        newItem: SendRouteDomain
    ): Boolean {
        return oldItem.recordNumber == newItem.recordNumber
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: SendRouteDomain,
        newItem: SendRouteDomain
    ): Boolean {
        return oldItem == newItem
    }
}
interface RouteArchiveListener {        // ???????????
    fun onDeleteClickK(itemDetail: SendRouteDomain)
    fun onDownloadClick(itemDetail: SendRouteDomain)
}