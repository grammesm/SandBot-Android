package com.alwaystinkering.sandbot.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.alwaystinkering.sandbot.data.SandBotRepository
import com.alwaystinkering.sandbot.model.pattern.AbstractPattern
import com.alwaystinkering.sandbot.utils.RefreshLiveData

class FilesViewModel internal constructor(
    sandBotRepository: SandBotRepository
) : ViewModel() {
    val fileList: LiveData<List<AbstractPattern>> =
        sandBotRepository.getFileList()
}