package dev.datlag.mimasu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.datlag.mimasu.core.typeOf
import dev.datlag.mimasu.tmdb.TMDB
import org.kodein.di.DirectDI
import org.kodein.di.instance
import kotlin.reflect.KClass

class KodeinViewModelFactory(private val di: DirectDI) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        return when {
            modelClass typeOf TrendingViewModel::class -> {
                val tmdb = di.instance<TMDB>()
                val model = TrendingViewModel(trendingRepository = tmdb.trending)

                (model as? T) ?: super.create(modelClass, extras)
            }
            else -> super.create(modelClass, extras)
        }
    }
}