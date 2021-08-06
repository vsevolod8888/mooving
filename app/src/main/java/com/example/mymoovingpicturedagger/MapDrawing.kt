package com.example.mymoovingpicture

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.dagger.App
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.launch
import javax.inject.Inject


class MapDrawing : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, GoogleMap.OnPolylineClickListener {
    private lateinit var mMap: GoogleMap
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    val viewModeDRaw: MapDrawingViewModel by viewModels { viewModelProvider }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapViewDrawing.onCreate(savedInstanceState)
        mapViewDrawing.onResume()
        mapViewDrawing.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)              // нажатие на кнопку my location
        mMap.setOnMyLocationClickListener(this)                    // нажатие на синюю точку
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                val loc = LatLng(
                    it.latitude,
                    it.longitude
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16f))
            }
        }

        mMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng: LatLng) {// Очищает ранее затронутую позицию


                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));// Перемещает камеру в новую позицию

                val location = LatLng(latlng.latitude, latlng.longitude)
                val latttitude = latlng.latitude
                val longitttude = latlng.longitude
                mMap.addMarker(
                    MarkerOptions().position(location).title("$latttitude + ,$longitttude")
                        .draggable(true)
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModeDRaw.saveDrawCoord(
                        latttitude,
                        longitttude,
                        System.currentTimeMillis(),
                        viewModeDRaw.lastNumberOfList()         //MAX(recordNumber) FROM coord +1
                    )
                }
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            val numberOfList = viewModeDRaw.lastNumberOfList()
            viewModeDRaw.getCoordByRecordNumber(numberOfList)
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer {

                    var distance = 0
                    val line: PolylineOptions = PolylineOptions()
                    line.clickable(true)
                    val latLngBuilder = LatLngBounds.Builder()
                    line.width(9f).color(Color.RED)
                    for (i in 0 until it.size) {
                        val latttitude = it.get(i).lattitude
                        val longitttude = it.get(i).longittude
                        val location = LatLng(latttitude, longitttude)
                        line.add(location)
                        latLngBuilder.include(location)
                        mMap.addMarker(
                            MarkerOptions().position(location).title("$latttitude + ,$longitttude")
                                .draggable(true)
                        )
                        if (i != it.size - 1) { // если координата в списке последняя, то подсчёт прекращаем, т.к. на предпоследней координате всё посчитали
                            val location2 =
                                LatLng(it.get(i + 1).lattitude, it.get(i + 1).longittude)
                            val meters: Double =
                                SphericalUtil.computeDistanceBetween(location, location2)

                            distance += meters.toInt()

                            if (distance < 1000) {
                                tvDrawRoute.setText("$distance м")
                            } else {
                                val a = distance / 1000
                                val b = distance % 1000
                                tvDrawRoute.setText("$a км $b м")
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
        val view: View = inflater.inflate(R.layout.map_drawing, container, false)
        val btnSave: Button = view.findViewById(R.id.buttonSave)


        btnSave.setOnClickListener {

            val nameOfDrRoute = viewModeDRaw.getSharedPreferencesRouteNameVM()

            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModeDRaw.getCoordinatesByRecordNumberSusVM(viewModeDRaw.lastNumberOfList()).size > 0) { //если точки есть, то сохр.

                    viewModeDRaw.saveDrawRoute(nameOfDrRoute!!, viewModeDRaw.lastNumberOfList())

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Марштут не сохранён, координаты не были записаны",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            this.findNavController().navigate(R.id.action_mapDrawing_to_fragmentCoordList)
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

    override fun onPolylineClick(p0: Polyline) {
        Toast.makeText(requireContext(), "Вы дали разрешение", Toast.LENGTH_SHORT).show()
    }

}