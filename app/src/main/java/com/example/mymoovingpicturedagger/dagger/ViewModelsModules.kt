package com.example.mymoovingpicturedagger.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mymoovingpicture.*
import com.example.mymoovingpicturedagger.*
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ForAllViewModelModule { // возвращает фабрику, чтобы можно было инжектить во вью модел

    @Binds
    @IntoMap
    @ViewModelKey(FragmentCoordListViewModel::class)
    abstract fun bindFragmentCoordViewModel(viewModel: FragmentCoordListViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(FragmentSignInViewModel::class)
    abstract fun bindFragmentSignInViewModel(viewModel: FragmentSignInViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(MapAllViewModel::class)
    abstract fun bindMapAllViewModel(viewModel: MapAllViewModel?): ViewModel?

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory?): ViewModelProvider.Factory? //должно провайдится только один раз для всех вью моделей

    @Binds
    @IntoMap
    @ViewModelKey(MapChosenRouteViewModel::class)
    abstract fun bindChoosenRouteViewModel(viewModel: MapChosenRouteViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(MapDrawingViewModel::class)
    abstract fun bindMapDrawViewModel(viewModel: MapDrawingViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(MapNewRouteViewModel::class)
    abstract fun bindMapNewRouteViewModel(viewModel: MapNewRouteViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(FragmentArchiveListViewModel::class)
    abstract fun bindArchiveListViewModel(viewModel: FragmentArchiveListViewModel?): ViewModel?//MainViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(FragmentAutrorizationViewModel::class)
    abstract fun bindFragmentAutrorizationViewModel(viewModel: FragmentAutrorizationViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(EnterRouteNameViewModel::class)
    abstract fun bindEnterRouteNameViewModel(viewModel: EnterRouteNameViewModel?): ViewModel?

    @Binds
    @IntoMap
    @ViewModelKey(FragmentNameDrawingRootViewModel::class)
    abstract fun bindFragmentNameDrawingRootViewModel(viewModel: FragmentNameDrawingRootViewModel?): ViewModel?

}




