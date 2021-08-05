package com.example.mymoovingpicturedagger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymoovingpicturedagger.adapter.AdapterArchive
import com.example.mymoovingpicturedagger.adapter.RouteArchiveListener
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.databinding.ArchiveListBinding
import com.example.mymoovingpicturedagger.domain.SendRouteDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class FragmentArchiveList: Fragment(), RouteArchiveListener {
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private val viewModel: FragmentArchiveListViewModel by viewModels { viewModelProvider }
    lateinit var adapter: AdapterArchive


    lateinit var binding: com.example.mymoovingpicturedagger.databinding.ArchiveListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as App).getappComponent().inject(this)
        binding = DataBindingUtil.inflate<ArchiveListBinding>(
            inflater,
            R.layout.archive_list, container, false
        )
        adapter = AdapterArchive(this)
        binding.coordRecyclerArchiveList.layoutManager = LinearLayoutManager(requireContext())
        binding.coordRecyclerArchiveList.adapter = adapter
        viewModel.listDomainArchiveRoutes.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)//
        })
        binding.buttonBackkk.setOnClickListener {
            findNavController()
                .navigate(R.id.action_fragmentArchiveList_to_fragmentCoordList)
        }


        return binding.root
    }

    override fun onDeleteClickK(itemDetail: SendRouteDomain) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteRouteFromServerVM(itemDetail.recordNumber)
            viewModel.refreshServerRoutesVM()
        }
    }
    override fun onDownloadClick(itemDetail: SendRouteDomain) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.downloadRouteFromServerVM(itemDetail.recordNumber)
            viewModel.refreshServerRoutesVM()
        }
    }
}