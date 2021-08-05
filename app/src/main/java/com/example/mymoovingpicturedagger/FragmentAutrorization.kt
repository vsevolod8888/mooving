package com.example.mymoovingpicturedagger

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymoovingpicturedagger.apiservice.OurApiService
import com.example.mymoovingpicturedagger.apiservice.retrofit
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.dto.RegistrationNewUserRequest
import com.google.android.material.button.MaterialButton
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject



class FragmentAutrorization : Fragment() {
    lateinit var enterUserName: EditText
    lateinit var enterEmail: EditText
    lateinit var enterPassword: EditText
    lateinit var buttonOkRegistrationReady: MaterialButton
    lateinit var buttonCancel: MaterialButton

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private val viewModel: FragmentAutrorizationViewModel by viewModels { viewModelProvider }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as App).getappComponent().inject(this)

        var rootView: View = inflater.inflate(R.layout.fr_register, container, false)
        enterUserName = rootView.findViewById(R.id.editTextUserName)
        enterEmail = rootView.findViewById(R.id.editTextEmail)
        enterPassword = rootView.findViewById(R.id.editTextEmail)
        buttonOkRegistrationReady = rootView.findViewById(R.id.buttonOkRegistrationReady)
        buttonCancel = rootView.findViewById(R.id.buttonBackTomainactivity)


        buttonOkRegistrationReady.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val newUser = RegistrationNewUserRequest(
                    username = enterUserName.text.toString(),
                    email = enterEmail.text.toString(),
                    password = enterPassword.text.toString()
                )
                viewModel.addNewUserVM(newUser)
                if (viewModel.addNewUserVM(newUser)==true){
                    Toast.makeText(requireContext(), "Регистрация прошла успешно", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_fragmentAutrorization_to_fragmentSignIn)
                }else{
                    Toast.makeText(requireContext(), "Не удалось зарегистрироваться", Toast.LENGTH_LONG).show()
                }
            }
        }
        buttonCancel.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentAutrorization_to_fragmentCoordList)
        }
        return rootView
    }


}