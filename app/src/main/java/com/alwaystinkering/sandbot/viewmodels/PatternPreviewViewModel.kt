package com.alwaystinkering.sandbot.viewmodels

import androidx.lifecycle.ViewModel
import com.alwaystinkering.sandbot.data.SandBotRepository
import com.alwaystinkering.sandbot.model.pattern.FileType

class PatternPreviewViewModel internal constructor(
    sandBotRepository: SandBotRepository,
    fileName: String,
    fileType: FileType
) : ViewModel() {

    val pattern = sandBotRepository.getFile(fileName, fileType)
}