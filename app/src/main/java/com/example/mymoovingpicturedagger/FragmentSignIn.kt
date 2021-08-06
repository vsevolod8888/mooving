package com.example.mymoovingpicturedagger

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.databinding.FragmentSigninBinding
import com.example.mymoovingpicturedagger.helpers.hideKeyboard
import com.example.mymoovingpicturedagger.helpers.viewBinding
import kotlinx.coroutines.launch
import javax.inject.Inject


class FragmentSignIn : Fragment(R.layout.fragment_signin) {

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory //где исп.вью модель, вставляем провайдер
    private val viewmodelSignIn: FragmentSignInViewModel by viewModels { viewModelProvider }
    private val viewmodelMain: MainViewModel by viewModels { viewModelProvider }

    private val bind by viewBinding(FragmentSigninBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).getappComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.buttonOkRegistration.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {

                val username = bind.editTextUserNameSignIn.text.toString()
                val password = bind.editTextPassport.text.toString()

                viewmodelSignIn.logInUser(username, password)
                viewmodelMain.checkIfUserEnterVM()
                if (viewmodelMain.enterUserNow.value != null) {
                    findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentCoordList)
                } else {
                    notSuccesfulEnter()
                    Toast.makeText(requireContext(), " Не удалось войти ", Toast.LENGTH_LONG).show()
                }
            }
            view?.hideKeyboard()
        }
        bind.buttonOkRegistration.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentAutrorization)
        }
        bind.buttonCancelSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentCoordList)
        }
    }



    fun notSuccesfulEnter() {
        val builder =
            android.app.AlertDialog.Builder(
                requireContext(),
                R.style.AlertDialogThemeee
            )      //, R.style.AlertDialogThemeee
        builder.setTitle("Не удалось войти")
            .setCancelable(false)
            .setPositiveButton(
                "Попробовать ещё"
            ) { dialog, which -> }
            .setNeutralButton("Регистрация") { dialog, which ->
                findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentAutrorization)
            }
            .setNegativeButton(
                "Назад"
            ) { dialog, which ->
                findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentCoordList)
            }
        val alert = builder.create()
        alert.setOnShowListener {
            val btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE)
            btnPositive.textSize = 25f
            btnPositive.setTextColor(resources.getColor(R.color.purple_200))//()

            val btnNeuteral = alert.getButton(Dialog.BUTTON_NEGATIVE)
            btnNeuteral.textSize = 25f
            btnNeuteral.setTextColor(resources.getColor(R.color.black))

            val btnNegative = alert.getButton(Dialog.BUTTON_NEUTRAL)
            btnNegative.textSize = 25f
            btnNegative.setTextColor(resources.getColor(R.color.purple_200))//()

            val layoutParams: LinearLayout.LayoutParams =
                btnPositive.layoutParams as LinearLayout.LayoutParams  // центрируем кнопки

            layoutParams.weight = 10F
            btnPositive.layoutParams = layoutParams
            btnNeuteral.layoutParams = layoutParams
            btnNegative.layoutParams = layoutParams
        }
        alert.show()
        val textViewId =
            alert.context.resources.getIdentifier("android:id/alertTitle", null, null)
        val tv = alert.findViewById<View>(textViewId) as TextView
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER)                            // заголовок по центру
        tv.setTextColor(resources.getColor(R.color.black))

    }
}