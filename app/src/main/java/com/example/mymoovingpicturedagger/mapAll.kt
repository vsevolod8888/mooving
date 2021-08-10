package com.example.mymoovingpicture

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymoovingpicturedagger.MapAllViewModel
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.databinding.MapAllBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class mapAll : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener, GoogleMap.OnPolylineClickListener {
    private lateinit var mMap: GoogleMap
    lateinit var binding: MapAllBinding

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    val viewModelMain: MapAllViewModel by viewModels { viewModelProvider }
    var distanceAll = 0
    val polylineMap =
        mutableMapOf<String, Long>()                  // kEY = POLYLINE ID, VALUE = ROUTE.ID
    val distanceMap =
        mutableMapOf<String, String>()
    lateinit var barChart: BarChart
    lateinit var listRoutesId: List<Long>
    val visitors = ArrayList<BarEntry>()
    val ourColours = ArrayList<Int>()
    val xvalues = ArrayList<String>()
    val polylinesMap =
        mutableMapOf<String, Polyline>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.mapViewAll.onCreate(savedInstanceState)
        binding.mapViewAll.onResume()
        binding.mapViewAll.getMapAsync(this)

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)               // нажатие на кнопку my location
        mMap.setOnMyLocationClickListener(this)                     // нажатие на синюю точку
        mMap.setOnPolylineClickListener(this)                       // передал кликлистенер н
        //  var id: Int? = arguments?.getInt("amount")
        lifecycleScope.launch {
            listRoutesId = // в чём отличие lastNumberOfList ??? это лист id из route
                viewModelMain.getOnlyIdListVM()  // лист из айдишек таблицы route
            Log.d("AAA", listRoutesId.toString())
            var i = 0
            listRoutesId.forEach {
                var distance = 0
                val line = PolylineOptions()
                line.clickable(true)
                val coordlist = viewModelMain.getCoordinatesBeIdSuspend(it)
                val latLngBuilder = LatLngBounds.Builder()
                line.width(8f)
                for (k in 0 until coordlist.size) {
                    val latttitude = coordlist.get(k).lattitude
                    val longitttude = coordlist.get(k).longittude
                    val location = LatLng(latttitude, longitttude)
                    line.add(location)
                    line.endCap
                    latLngBuilder.include(location)
                    if (k != coordlist.size - 1) { // если координата в списке последняя, то подсчёт прекращаем, т.к. на предпоследней координате всё посчитали
                        val location2 =
                            LatLng(coordlist.get(k + 1).lattitude, coordlist.get(k + 1).longittude)
                        val meters: Double =
                            SphericalUtil.computeDistanceBetween(location, location2)
                        distance += meters.toInt()
                        distanceAll += meters.toInt()      // вся длина всех маршрутов
                        setDistanseToTextView(distanceAll)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                    }
                }
                when (it % 30) {
                    in 0..2 -> line.color(Color.GREEN)
                    in 3..4 -> line.color(Color.CYAN)
                    in 5..6 -> line.color(Color.RED)
                    in 7..8 -> line.color(Color.YELLOW)
                    in 9..10 -> line.color(Color.BLACK)
                    in 11..12 -> line.color(Color.BLUE)
                    in 13..14 -> line.color(Color.DKGRAY)
                    in 15..16 -> line.color(Color.LTGRAY)
                    in 17..18 -> line.color(Color.BLUE)
                    in 19..29 -> line.color(Color.MAGENTA)
                    else -> line.color(Color.BLACK)
                }
                if (coordlist.size > 0) {
                    val polyline =
                        mMap.addPolyline(line)     // Map.addPolyline создаёт полилинию
                    polylineMap.put(polyline.id, coordlist[0].recordNumber)

                    if (distance < 1000) {
                        distanceMap.put(polyline.id, "$distance м")
                    } else {
                        val a = distance / 1000
                        val b = distance % 1000
                        distanceMap.put(polyline.id, "$a км $b м")
                    }
                    polylinesMap.put(polyline.id, polyline)
                }

                val data = BarEntry(
                    i++.toFloat(),
                    distance.toFloat()
                )
                visitors.add(data)


                ourColours.add(
                    when (it % 30) {
                        in 0..2 -> Color.GREEN
                        in 3..4 -> Color.CYAN
                        in 5..6 -> Color.RED
                        in 7..8 -> Color.YELLOW
                        in 9..10 -> Color.BLACK
                        in 11..12 -> Color.BLUE
                        in 13..14 -> Color.DKGRAY
                        in 15..16 -> Color.LTGRAY
                        in 17..18 -> Color.BLUE
                        in 19..29 -> Color.MAGENTA
                        else -> Color.BLACK
                    }
                )
                xvalues.add(timeToString(coordlist[0].time))
            }

            val barDataSet = BarDataSet(visitors, "Маршруты")
            barDataSet.colors = ourColours//ColorTemplate.COLORFUL_COLORS.toMutableList()

            barDataSet.valueTextColor = resources.getColor(R.color.black)
            barDataSet.valueTextSize = 16f
            val barData = BarData(barDataSet)
            barChart.setFitBars(true)
            barChart.data = barData
            val xAxis = barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(xvalues)
            xAxis.labelRotationAngle = 270F
            xAxis.textSize = 20F
            xAxis.setGranularity(1f)
            val yAxisLeft: YAxis = barChart.axisLeft
            val yAxisRight: YAxis = barChart.axisRight
            val custom = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {

                    return value.toInt().toString() + " м"
                }
            }

            yAxisLeft.valueFormatter = custom//ValueFormatter?
            yAxisRight.valueFormatter = custom
            binding.barChart.description.text = "Приветик"
            binding.barChart.animateY(2000)
            // barChart.animateX(2000)
            binding.barChart.invalidate()
        }
        mMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng: LatLng) {// Очищает ранее затронутую позицию

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));// Перемещает камеру в новую позицию
                val location = LatLng(latlng.latitude, latlng.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
                polylinesMap.forEach {
                    val pol: Polyline = it.value
                    pol.width = 8f
                }
                setDistanseToTextView(distanceAll)
                binding.tvTwo.setText("")

            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity().application as App).getappComponent().inject(this)
        binding = DataBindingUtil.inflate<MapAllBinding>(
            inflater,
            R.layout.map_all, container, false
        )


        binding.buttonGraphicBack.visibility = View.GONE
        binding.buttonBack.setOnClickListener {
            this.findNavController().navigate(R.id.action_mapAll_to_fragmentCoordList)
        }


        binding.barChart.visibility = View.GONE

        binding.buttonGraphic.setOnClickListener {
            barChart.visibility = View.VISIBLE
            binding.buttonGraphicBack.visibility = View.VISIBLE
            binding.buttonGraphic.visibility = View.GONE
            binding.buttonBack.visibility = View.GONE
            binding.mapViewAll.alpha = 0.3F
            binding.tvAll.alpha = 0.3F
        }
        binding.buttonGraphicBack.setOnClickListener {
            barChart.visibility = View.GONE
            binding.buttonGraphicBack.visibility = View.GONE
            binding.buttonBack.visibility = View.VISIBLE
            binding.buttonGraphic.visibility = View.VISIBLE
            binding.mapViewAll.alpha = 1F
            binding.tvAll.alpha = 1F
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

    override fun onPolylineClick(p0: Polyline) {
        //val i:Int = viewModelMain.
        val rounenumber =
            polylineMap.get(p0.id) // по клику на полилинию дост id маршрута, а по ней всё остальное из бд
        val d = distanceMap.get(p0.id)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.tvAll.setText("$d")
            val infa = viewModelMain.getInfoById(rounenumber!!)
            binding.tvTwo.setText(infa)
            selectedPol(p0.id)
        }
    }

    fun selectedPol(id: String) {
        val selectedPol: Polyline? = polylinesMap.get(id)        // sise = 9, id = pl8  id.toInt()
        //   selectedPol!!.width = 16f
        polylinesMap.forEach {
            val pol: Polyline = it.value
            if (pol != selectedPol) {
                pol!!.width = 8f
            } else {
                pol.width = 22f
            }
        }
    }

    fun setDistanseToTextView(dist: Int) {
        if (dist < 1000) {
            binding.tvAll.setText("$dist м")
        } else {
            val a = dist / 1000
            val b = dist % 1000
            binding.tvAll.setText("$a км $b м")
        }
    }

    fun timeToString(t: Long): String {
        t.let {
            val locale = Locale("ru", "RU")
            val format2: DateFormat = SimpleDateFormat(("dd MMM"), locale)
            val daytoday: String = format2.format(t).capitalize()
            return daytoday
        }
    }
}





