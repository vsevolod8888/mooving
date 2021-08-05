package com.example.mymoovingpicture

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mymoovingpicturedagger.EnterRouteNameViewModel
import com.example.mymoovingpicturedagger.FragmentArchiveListViewModel
import com.example.mymoovingpicturedagger.R
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.foreground_service.ForegroundService
import com.google.android.material.button.MaterialButton
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import javax.inject.Inject


class EnterRouteName : Fragment() {
    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private val viewModel: EnterRouteNameViewModel by viewModels { viewModelProvider }
    lateinit var enterName: EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as App).getappComponent().inject(this)
        var rootView: View = inflater.inflate(R.layout.enter_route_name, container, false)
        enterName = rootView.findViewById(R.id.editTextRouteName)
        val btnEnterRouteName: MaterialButton = rootView.findViewById(R.id.buttonok)
        val btnBack: MaterialButton = rootView.findViewById(R.id.buttonbackn)
        val rg: RadioGroup = rootView.findViewById(R.id.radioGroupp)
        val lastButtonState: Int? = viewModel.getSharedPreferencesTimeRepeatVM()

        when (lastButtonState) {
            6000 -> {
                val radBut1: RadioButton = rootView.findViewById(R.id.radioButton1)
                radBut1.isChecked = true
            }
            60000 -> {
                val radBut2: RadioButton = rootView.findViewById(R.id.radioButton2)
                radBut2.isChecked = true
            }
            6000000 -> {
                val radBut3: RadioButton = rootView.findViewById(R.id.radioButton3)
                radBut3.isChecked = true
            }
        }
        lastButtonState!!.equals(1)

        btnEnterRouteName.setOnClickListener {
            val selectedId: Int = rg.checkedRadioButtonId
            when (selectedId) {
                R.id.radioButton1 -> {
                    ForegroundService.timeRepeat = 6000
                    viewModel.putSharedPreferencesTimeRepeatVM(6000)
                }

                R.id.radioButton2 -> {
                    ForegroundService.timeRepeat = 60000
                    viewModel.putSharedPreferencesTimeRepeatVM(60000)
                }
                R.id.radioButton3 -> {
                    viewModel.putSharedPreferencesTimeRepeatVM(600000)
                }
            }
            checkPermissionAndStart()
        }
        btnBack.setOnClickListener{
            findNavController().navigate(R.id.action_enterRouteName_to_fragmentCoordList)
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
                    if (
                        checkIsLocationIsTurned()
                    ) {
                        ForegroundService.startService(requireContext(), routeName)
                        this@EnterRouteName.findNavController()
                            .navigate(R.id.action_enterRouteName_to_mapNewRoute)
                    }

                } // startLocationUpdates()
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