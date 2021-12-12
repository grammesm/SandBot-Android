package com.alwaystinkering.sandbot.ui.playlistedit

import androidx.lifecycle.ViewModel
import com.alwaystinkering.sandbot.repo.SandbotRepository
import javax.inject.Inject

class SequenceEditViewModel @Inject constructor(sandBotRepository: SandbotRepository) :
    ViewModel() {
    val fileList = sandBotRepository.getFileList()
}