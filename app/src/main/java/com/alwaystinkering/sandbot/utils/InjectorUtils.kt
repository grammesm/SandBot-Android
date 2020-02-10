package com.alwaystinkering.sandbot.utils

import android.content.Context
import com.alwaystinkering.sandbot.data.SandBotRepository
import com.alwaystinkering.sandbot.model.pattern.FileType
import com.alwaystinkering.sandbot.viewmodels.BotViewModelFactory
import com.alwaystinkering.sandbot.viewmodels.FilesViewModelFactory
import com.alwaystinkering.sandbot.viewmodels.PatternPreviewViewModel
import com.alwaystinkering.sandbot.viewmodels.PatternPreviewViewModelFactory


/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getSandBotRepository(context: Context): SandBotRepository {
        return SandBotRepository.getInstance()
    }

    fun provideBotViewModelFactory(
        context: Context
    ): BotViewModelFactory{
        val repository = getSandBotRepository(context)
        return BotViewModelFactory(repository)
    }

    fun provideFilesViewModelFactory(
        context: Context
    ): FilesViewModelFactory{
        val repository = getSandBotRepository(context)
        return FilesViewModelFactory(repository)
    }

    fun providePatternPreviewViewModelFactory(
        context: Context,
        fileName: String,
        fileType: FileType
    ): PatternPreviewViewModelFactory {
        val repository = getSandBotRepository(context)
        return PatternPreviewViewModelFactory(repository, fileName, fileType)
    }}
