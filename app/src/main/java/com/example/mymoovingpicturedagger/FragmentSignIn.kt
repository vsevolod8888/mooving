package com.example.mymoovingpicturedagger

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymoovingpicturedagger.dagger.App
import com.example.mymoovingpicturedagger.repozitory.Repozitory
import com.google.android.material.button.MaterialButton
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import javax.inject.Inject




class FragmentSignIn : Fragment() {
    lateinit var enterUserNameSignIn: EditText
    lateinit var enterPasswordSignIn: EditText
    lateinit var buttonOkGo: MaterialButton
    lateinit var buttonNeedRegistration: MaterialButton
    lateinit var cancelBtn:MaterialButton

        @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory //где исп.вью модель, вставляем провайдер
    private val viewmodelSignIn: FragmentSignInViewModel by viewModels { viewModelProvider }
    private val viewmodelMain: MainViewModel by viewModels { viewModelProvider }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().application as App).getappComponent().inject(this)
        var rootView: View = inflater.inflate(R.layout.fragment_signin, container, false)
        enterUserNameSignIn = rootView.findViewById(R.id.editTextUserNameSignIn)
        enterPasswordSignIn = rootView.findViewById(R.id.editTextPassport)
        buttonOkGo = rootView.findViewById(R.id.buttonOkRegistration)
        buttonNeedRegistration = rootView.findViewById(R.id.buttonNeedRegistration)
        cancelBtn = rootView.findViewById(R.id.buttonCancelSignIn)

        buttonOkGo.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {

                  var  username:String = enterUserNameSignIn.text.toString()
                  var  password:String = enterPasswordSignIn.text.toString()

                viewmodelSignIn.logInUser(username,password)
                viewmodelMain.checkIfUserEnterVM()
                if (viewmodelMain.enterUserNow.value!=null){
                        findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentCoordList)
                }else{
                    notSuccesfulEnter()
                    Toast.makeText(requireContext(), " Не удалось войти ", Toast.LENGTH_LONG).show()
                }
            }
            view?.let {
                activity?.hideKeyboard(it)
            }
        }
        buttonNeedRegistration.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentAutrorization)
        }
        cancelBtn.setOnClickListener{
            findNavController().navigate(R.id.action_fragmentSignIn_to_fragmentCoordList)
        }
        return rootView
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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