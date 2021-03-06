package com.example.mymoovingpicture

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.databinding.ArchiveListBinding
import com.example.mymoovingpicturedagger.databinding.MapChoosenRBinding
import com.example.mymoovingpicturedagger.foreground_service.ForegroundService
import com.example.mymoovingpicturedagger.helpers.viewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.button.MaterialButton
import com.google.maps.android.SphericalUtil
//import kotlinx.android.synthetic.main.map_choosen_r.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class MapChosenRoute : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {
    private lateinit var mMap: GoogleMap
    lateinit var binding: com.example.mymoovingpicturedagger.databinding.MapChoosenRBinding

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    val viewModelMapChoosenRoute: MapChosenRouteViewModel by viewModels { viewModelProvider }


    var idd: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.mapViewChoosen.onCreate(savedInstanceState)
        binding.mapViewChoosen.onResume()
        binding.mapViewChoosen.getMapAsync(this)
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)               // ?????????????? ???? ???????????? my location
        mMap.setOnMyLocationClickListener(this)                     // ?????????????? ???? ?????????? ??????????
        idd = arguments?.getLong("amount")                     //?????????????? ?? ?????? ???????? ???? ???????? ????????
        idd?.let {
            viewModelMapChoosenRoute.getCoordinatesBeId(it)
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    var distance = 0
                    val idRoute = it[0].recordNumber
                    viewLifecycleOwner.lifecycleScope.launch {
                        val name = viewModelMapChoosenRoute.getRouteById(idRoute)!!.recordRouteName
                        binding.tvTittle.setText(name)
                    }
                    val line: PolylineOptions = PolylineOptions()
                    line.clickable(true)
                    val latLngBuilder = LatLngBounds.Builder()

                    line.width(9f).color(Color.RED)
                    for (i in 0 until it.size) {
                        val latttitude = it.get(i).lattitude
                        val longitttude = it.get(i).longittude
                        val location = LatLng(latttitude, longitttude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                        line.add(location)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                        latLngBuilder.include(location)
                        if (i != it.size - 1) { // ???????? ???????????????????? ?? ???????????? ??????????????????, ???? ?????????????? ????????????????????, ??.??. ???? ?????????????????????????? ???????????????????? ?????? ??????????????????
                            val location2 =
                                LatLng(it.get(i + 1).lattitude, it.get(i + 1).longittude)
                            val meters: Double =
                                SphericalUtil.computeDistanceBetween(location, location2)
                            distance += meters.toInt()

                            if (distance < 1000) {
                                binding.tvChoosen.setText("$distance ??")
                            } else {
                                //distance %= 1000
                                val a = distance / 1000
                                val b = distance % 1000
                                binding.tvChoosen.setText("$a ???? $b ??")
                            }

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                        }
                    }

                    mMap.addPolyline(line)
                })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as App).getappComponent().inject(this)
        binding = DataBindingUtil.inflate<MapChoosenRBinding>(
            inflater,
            R.layout.map_choosen_r, container, false
        )
        binding.buttonStop.visibility = View.GONE
        binding.buttonBack.setOnClickListener {
            this.findNavController().navigate(R.id.action_mapChosenRoute_to_fragmentCoordList)
        }
        binding.buttonContinue.setOnClickListener {

            val builder =
                android.app.AlertDialog.Builder(
                    requireContext(),
                    R.style.AlertDialogThemeee
                )
            builder.setTitle("?????????????? ?????????????????????? ????????????????????????????")
                .setCancelable(false)
                .setPositiveButton(
                    "10 ??????"
                ) { dialog, which ->
                    ForegroundService.timeRepeat = 600000
                    ForegroundService.startService(requireContext(), "", idd.toString())
                    binding.buttonContinue.visibility = View.GONE
                    binding.buttonStop.visibility = View.VISIBLE
                }
                .setNeutralButton(
                    "6 ??????"
                ) { dialog, which ->
                    ForegroundService.timeRepeat = 6000
                    ForegroundService.startService(requireContext(), "", idd.toString())
                    binding.buttonContinue.visibility = View.GONE
                    binding.buttonStop.visibility = View.VISIBLE
                }
                .setNegativeButton(
                    "1 ??????"
                ) { dialog, which ->
                    ForegroundService.timeRepeat = 60000
                    ForegroundService.startService(requireContext(), "", idd.toString())
                    binding.buttonContinue.visibility = View.GONE
                    binding.buttonStop.visibility = View.VISIBLE
                }
            val alert = builder.create()
            alert.setOnShowListener {
                val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
                btnPositive.textSize = 14f
                btnPositive.setTextColor(resources.getColor(R.color.purple_200))//()

                val btnNeuteral = alert.getButton(Dialog.BUTTON_NEUTRAL)
                btnNeuteral.textSize = 10f
                btnNeuteral.setTextColor(resources.getColor(R.color.purple_200))

                val btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE)
                btnNeuteral.textSize = 14f
                btnNegative.setTextColor(resources.getColor(R.color.purple_200))

                val layoutParams: LinearLayout.LayoutParams =
                    btnPositive.layoutParams as LinearLayout.LayoutParams  // ???????????????????? ????????????
                layoutParams.weight = 10F
                btnPositive.layoutParams = layoutParams
                btnNeuteral.layoutParams = layoutParams
                btnNegative.layoutParams = layoutParams
            }
            alert.show()
            val textViewId =
                alert.context.resources.getIdentifier("android:id/alertTitle", null, null)
            val tv = alert.findViewById<View>(textViewId) as TextView
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER)                            // ?????????????????? ???? ????????????
            tv.setTextColor(resources.getColor(R.color.black))
        }
        binding.buttonStop.setOnClickListener {
            ForegroundService.stopService(requireContext())
            this.findNavController().navigate(R.id.action_mapChosenRoute_to_fragmentCoordList)
        }
        return binding.root
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(context, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }
}