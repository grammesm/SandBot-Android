package com.alwaystinkering.sandbot.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alwaystinkering.sandbot.data.SandBotRepository
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.model.pattern.FileType

/**
 * Factory for creating a [PatternPreviewViewModel] with a constructor that takes a
 * [SandBotRepository] and [AbstractPattern].
 */
class PatternPreviewViewModelFactory(
    private val repository: SandBotRepository,
    private val fileName: String,
    private val fileType: FileType

) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PatternPreviewViewModel(repository, fileName, fileType) as T
    }
}