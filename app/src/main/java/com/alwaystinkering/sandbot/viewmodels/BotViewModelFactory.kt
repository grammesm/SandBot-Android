package com.alwaystinkering.sandbot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alwaystinkering.sandbot.data.SandBotRepository

/**
 * Factory for creating a [BotViewModel] with a constructor that takes a
 * [SandBotRepository].
 */
class BotViewModelFactory(
    private val repository: SandBotRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BotViewModel(repository) as T
    }
}