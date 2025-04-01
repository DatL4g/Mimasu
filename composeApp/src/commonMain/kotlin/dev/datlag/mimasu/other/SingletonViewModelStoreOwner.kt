package dev.datlag.mimasu.other

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

data object SingletonViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()
}