package com.example.mymoovingpicture

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymoovingpicturedagger.MainViewModel
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.adapter.Adapter
import com.example.mymoovingpicturedagger.adapter.RouteListener
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.databinding.FragmentCoordListBinding
import com.example.mymoovingpicturedagger.domain.RouteDomain
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

import javax.inject.Inject

class FragmentCoordList() : Fragment(), RouteListener {
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory //где исп.вью модель, вставляем провайдер
    private val viewModel: FragmentCoordListViewModel by viewModels { viewModelProvider }// by activityViewModels()//одна вьюмодель на все фрагменты//одгна вьюмодель на все фрагменты
    private val viewModelMain: MainViewModel by viewModels { viewModelProvider }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    lateinit var adapter: Adapter

    lateinit var binding: com.example.mymoovingpicturedagger.databinding.FragmentCoordListBinding

    @SuppressLint("ResourceType", "RtlHardcoded")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as App).getappComponent().inject(this)
        binding = DataBindingUtil.inflate<FragmentCoordListBinding>(
            inflater,
            R.layout.fragment_coord_list, container, false
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        adapter = Adapter(this)

        binding.coordRecyclerList.layoutManager = LinearLayoutManager(requireContext())
        binding.coordRecyclerList.adapter = adapter

        swipeToDeleteHelper.attachToRecyclerView(binding.coordRecyclerList)

        viewModel.routeList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)//

        })
        viewModel.choosenroute.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val id = it.id
            val bundle = bundleOf("amount" to id)
            this.findNavController()
                .navigate(R.id.action_fragmentCoordList_to_mapChosenRoute, bundle)
            Log.d("ОШИБКА", id.toString())
        })
        viewModel.event.onEach {
            when(it){
                FragmentCoordListViewModel.Event.Registration -> {
                    registration()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)



        binding.buttonNewRoute.setOnClickListener {
            findNavController()
                .navigate(R.id.action_fragmentCoordList_to_enterRouteName)             //перех в ENTERROUTENAME FRAGment
        }

        binding.buttonDraw.setOnClickListener {
            findNavController()
                .navigate(R.id.action_fragmentCoordList_to_fragmentNameDrawingRoot)
        }
        return binding.root
    }

    private val swipeToDeleteHelper = ItemTouchHelper(object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val pos = viewHolder.adapterPosition
            val builder = android.app.AlertDialog.Builder(
                requireContext(),
                R.style.AlertDialogThemeee
            )
            builder.setTitle("Удалить маршрут ${viewModel.routeList.value?.get(pos)!!.recordRouteName}?")
                .setCancelable(false)
                .setPositiveButton(
                    "Удалить"
                ) { dialog, which ->
                    var route = viewModel.routeList.value!!.get(pos)

                    viewLifecycleOwner.lifecycleScope.launch {
                        Log.d("JJJ", "Привет${viewModel.routeList.value?.get(pos)!!.id}")//
                        Toast.makeText(
                            requireContext(),
                            route.recordRouteName + " удалён",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.deleteRouteAndRecordNumberTogether(route.id)            //-1
                    }
                }
                .setNegativeButton(
                    "Отмена"
                ) { dialog, which ->
                    binding.coordRecyclerList.getAdapter()!!
                        .notifyItemChanged(viewHolder.adapterPosition)
                }
            val alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.textSize = 25f
                btnPositive.setTextColor(resources.getColor(R.color.purple_200))//()
                val btnNeuteral = alert.getButton(Dialog.BUTTON_NEGATIVE)
                btnNeuteral.textSize = 25f
                btnNeuteral.setTextColor(resources.getColor(R.color.black))
                val layoutParams: LinearLayout.LayoutParams =
                    btnPositive.layoutParams as LinearLayout.LayoutParams  // центрируем кнопки
                layoutParams.weight = 10F
                btnPositive.layoutParams = layoutParams
                btnNeuteral.layoutParams = layoutParams
            }
            alert.show()
            val textViewId =
                alert.context.resources.getIdentifier("android:id/alertTitle", null, null)
            val tv = alert.findViewById<View>(textViewId) as TextView
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER)                            // заголовок по центру
            tv.setTextColor(resources.getColor(R.color.black))
        }
    })

    override fun onClickK(itemDetail: RouteDomain) {
        viewModel.onClickDetail(itemDetail)
        Toast.makeText(requireContext(), "Сработал OnClikk", Toast.LENGTH_LONG).show()
    }

    override fun onUploadOnSErverClick(itemDetail: RouteDomain) { // проверить, если шаред преф пустой, то перекидывать на фрагмент логина и регистрации
        if (viewModelMain.enterUserNow.value!=null){
            viewModel.uploadRouteOnServer(itemDetail)
        }else{
            viewModel.registration()
        }



    }
    fun registration(){
        val builder =
            android.app.AlertDialog.Builder(
                requireContext(),
                R.style.AlertDialogThemeee
            )      //, R.style.AlertDialogThemeee
        builder.setTitle("Необходимо войти в аккаунт")
            .setCancelable(false)
            .setPositiveButton(
                "Войти"
            ) { dialog, which ->
                this.findNavController().navigate(R.id.action_fragmentCoordList_to_fragmentSignIn)
            }
            .setNegativeButton(
                "Отмена"
            ) { dialog, which ->
            }
        val alert = builder.create()
        alert.setOnShowListener {
            val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
            btnPositive.textSize = 25f
            // btnPositive.gravity = Gravity.CENTER
            btnPositive.setTextColor(requireContext().resources.getColor(R.color.purple_200))//()
            val btnNeuteral = alert.getButton(Dialog.BUTTON_NEGATIVE)
            btnNeuteral.textSize = 25f
            // btnNeuteral.gravity = Gravity.CENTER
            btnNeuteral.setTextColor(requireContext().resources.getColor(R.color.black))
            val layoutParams: LinearLayout.LayoutParams =
                btnPositive.layoutParams as LinearLayout.LayoutParams  // центрируем кнопки
            layoutParams.weight = 10F
            btnPositive.layoutParams = layoutParams
            btnNeuteral.layoutParams = layoutParams
        }
        alert.show()
        val textViewId =
            alert.context.resources.getIdentifier("android:id/alertTitle", null, null)
        val tv = alert.findViewById<View>(textViewId) as TextView
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER)                            // заголовок по центру
        tv.setTextColor(requireContext().resources.getColor(R.color.black))
    }
}


