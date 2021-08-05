package com.example.mymoovingpicturedagger

//import com.example.mymoovingpicturedagger.databinding.ActivityMainnBinding

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.mymoovingpicture.FragmentCoordListViewModel
import com.example.mymoovingpicturedagger.dagger.App
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    val REQUEST_CODE: Int = 1


    lateinit var tvLogin: TextView
    lateinit var tvEmail: TextView
    lateinit var imageViewww: CircleImageView

    lateinit var btnUpload: MaterialButton

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory //где исп.вью модель, вставляем провайдер
    private val viewmodelMain: MainViewModel by viewModels { viewModelProvider }
    private val viewmodel: FragmentCoordListViewModel by viewModels { viewModelProvider }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainn)
        (application as App).getappComponent().inject(this)
        val drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val navView: NavigationView = findViewById(R.id.navigationView)
        tvLogin = navView.getHeaderView(0).findViewById(R.id.tvNickname)
        tvEmail = navView.getHeaderView(0).findViewById(R.id.tvEmail)
        imageViewww = navView.getHeaderView(0).findViewById(R.id.profile_image)
        btnUpload = navView.getHeaderView(0).findViewById(R.id.buttonUploadPhoto)
        lifecycleScope.launch {
            viewmodelMain.checkIfUserEnterVM()
        }

        viewmodelMain.enterUserNow.observe(this, Observer {
            if (it != null) {                                     // переместить это в обзёрв
                navView.menu.findItem(R.id.loginOrLogout).icon =
                    resources.getDrawable(R.drawable.logout)
                navView.menu.findItem(R.id.loginOrLogout).title = "Выйти"
                tvLogin.text = it.username
                tvEmail.text = it.email
                Glide.with(this)
                    .load(it.photo)
                    .into(imageViewww)

            } else {
                navView.menu.findItem(R.id.loginOrLogout).icon =
                    resources.getDrawable(R.drawable.login)
                navView.menu.findItem(R.id.loginOrLogout).title = "Войти"
                tvLogin.text = "Имя"
                tvEmail.text = "Email"
                imageViewww.setImageResource(R.drawable.profile)
            }
        })

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.download -> {
                    goToDownloadFragment()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.allRoutes -> {
                    goToMapAllFragment()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.deleteAll -> {
                    deleteAllRoutes()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.loginOrLogout -> {
                    if (viewmodelMain.enterUserNow.value != null) {
                        Toast.makeText(
                            this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT
                        ).show()// очищаю токен и делаю обновление информаци о юзере
                        lifecycleScope.launch {
                            viewmodelMain.toMakeTokenNull()
                            viewmodelMain.checkIfUserEnterVM()
                        }
                    } else {
                        Navigation.findNavController(this@MainActivity,
                            R.id.myNavHostFragment1)
                            .navigate(R.id.action_fragmentCoordList_to_fragmentSignIn)

                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
        btnUpload.setOnClickListener {
            checkPermissionAndStart()
        }
    }

    private fun openGalleryForImage() {
        if (viewmodelMain.enterUserNow.value != null) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE)
        } else {
            Toast.makeText(this@MainActivity, "Вы не зашли в аккаунт",
                Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            Log.d("OOO", "Зашло в onActivityResult")

            lifecycleScope.launch {
                viewmodelMain.uploadPhotoOnServerVM(data!!.data!!)
                viewmodelMain.checkIfUserEnterVM()
            }
        }
    }

    fun checkPermissionAndStart() {
        var permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@MainActivity, "Вы дали разрешение",
                    Toast.LENGTH_SHORT).show()
                openGalleryForImage()

            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(
                    this@MainActivity,
                    "Отказано в разрешении\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permissionlistener)
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun deleteAllRoutes() {
        val builder =
            android.app.AlertDialog.Builder(
                this,
                R.style.AlertDialogThemeee
            )
        builder.setTitle("  Удалить все маршруты?")
            .setCancelable(false)
            .setPositiveButton(
                "Удалить"
            ) { dialog, which ->
                lifecycleScope.launch {
                    viewmodelMain.deleteListAllRoutes()
                }
            }
            .setNegativeButton(
                "Отмена"
            ) { dialog, which ->
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

    private fun goToDownloadFragment() {
        val token = viewmodelMain.getToken()
        if (token != null) {
            Navigation.findNavController(this@MainActivity, R.id.myNavHostFragment1)
                .navigate(R.id.action_fragmentCoordList_to_fragmentArchiveList)
        } else {
            viewmodel.registration()
        }
    }

    private fun goToMapAllFragment() {
        lifecycleScope.launch {
            if (viewmodelMain.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "Список маршрутов пуст",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Navigation.findNavController(this@MainActivity, R.id.myNavHostFragment1)
                    .navigate(R.id.action_fragmentCoordList_to_mapAll)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}