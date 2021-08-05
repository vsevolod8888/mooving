package com.example.mymoovingpicture

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.domain.CoordinatesDomain
import com.example.mymoovingpicturedagger.foreground_service.ForegroundService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.map_choosen_r.*
import kotlinx.android.synthetic.main.map_new_r.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapNewRoute : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {
    private lateinit var mMap: GoogleMap
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    val viewModelMapNewRoute: MapNewRouteViewModel by viewModels {viewModelProvider}
    //var distance = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapViewNew.onCreate(savedInstanceState)
        mapViewNew.onResume()
        mapViewNew.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)               // нажатие на кнопку my location
        mMap.setOnMyLocationClickListener(this)                     // нажатие на синюю точку
        viewLifecycleOwner.lifecycleScope.launch {
            val numberOfList = viewModelMapNewRoute.lastNumberOfList()
            viewModelMapNewRoute.getCoordByRecordNumber(numberOfList).observe( // список координат по последнему id из списка маршрутов
                viewLifecycleOwner,
                Observer {// наблюдаем за списком координат под посл
                    var distance = 0
                    val line: PolylineOptions = PolylineOptions()
                    line.clickable(true)
                    val latLngBuilder = LatLngBounds.Builder()
                    line.width(9f).color(Color.RED)
                    for (i in 0 until it.size ) {
                        val latttitude = it.get(i).lattitude
                        val longitttude = it.get(i).longittude
                        val location = LatLng(latttitude, longitttude)
                        line.add(location)
                        latLngBuilder.include(location)
                        mMap.addMarker(
                            MarkerOptions().position(location).title("$latttitude + ,$longitttude")
                                .draggable(true)
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                        if(i!=it.size-1){ // если координата в списке последняя, то подсчёт прекращаем, т.к. на предпоследней координате всё посчитали
                            val location2 = LatLng(it.get(i + 1).lattitude, it.get(i + 1).longittude)
                            val meters: Double =
                                SphericalUtil.computeDistanceBetween(location, location2)
                            distance += meters.toInt()
                            if(distance<1000){
                                tvNew.setText("$distance м")
                            }else{
                                //distance %= 1000
                                val a = distance /1000
                                val b = distance %1000
                                tvNew.setText("$a км $b м")
                            }
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
        val view: View = inflater.inflate(R.layout.map_new_r, container, false)
        val btnStop: Button = view.findViewById(R.id.buttonStopService)


        btnStop.setOnClickListener {
            ForegroundService.stopService(requireContext())
            this.findNavController().navigate(R.id.action_mapNewRoute_to_fragmentCoordList)
        }
        return view
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


