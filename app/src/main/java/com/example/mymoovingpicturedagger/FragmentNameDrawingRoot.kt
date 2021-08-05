package com.example.mymoovingpicture

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mymoovingpicturedagger.FragmentNameDrawingRootViewModel
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.dagger.App
import com.google.android.material.button.MaterialButton
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import javax.inject.Inject

class FragmentNameDrawingRoot :Fragment(){
    lateinit var enterName: EditText

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    val viewModelDrawing: FragmentNameDrawingRootViewModel by viewModels {viewModelProvider}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as App).getappComponent().inject(this)
        var rootView: View = inflater.inflate(R.layout.enter_drawing_route_name, container, false)
        enterName= rootView.findViewById(R.id.editTextDrawRouteName)
        val btnBack:MaterialButton = rootView.findViewById(R.id.buttonbackdrawing)
        val btnEnterDrawRouteName: Button = rootView.findViewById(R.id.buttonokdrawing)


        btnEnterDrawRouteName.setOnClickListener {
            checkPermissionAndStart()

        }
        btnBack.setOnClickListener {
           findNavController().navigate(R.id.action_fragmentNameDrawingRoot_to_fragmentCoordList)

        }
        return rootView
    }

    fun checkPermissionAndStart() {
        var permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(requireContext(), "Вы дали разрешение", Toast.LENGTH_SHORT).show()
                val routeName: String = enterName.text.toString()
                if (routeName.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Введите название маршрута",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (checkIsLocationIsTurned()){
                        viewModelDrawing.putSharedPreferencesRouteNameVM(routeName)
                        this@FragmentNameDrawingRoot.findNavController()
                            .navigate(R.id.action_fragmentNameDrawingRoot_to_mapDrawing)
                    }

                }
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(
                    requireContext(),
                    "Отказано в разрешении\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        TedPermission.with(requireContext())
            .setPermissionListener(permissionlistener)
            .setPermissions(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }

    fun checkIsLocationIsTurned(): Boolean {
        val lm: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }



        if (!gps_enabled ) {
            // notify user
            AlertDialog.Builder(context)
                .setMessage("Включить локацию?")
                .setPositiveButton("Да", object : DialogInterface.OnClickListener {
                    override fun onClick(
                        paramDialogInterface: DialogInterface?,
                        paramInt: Int
                    ) {
                        context!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                })
                .setNegativeButton("Нет", null)
                .show()
        }
        return (gps_enabled)
    }
}


