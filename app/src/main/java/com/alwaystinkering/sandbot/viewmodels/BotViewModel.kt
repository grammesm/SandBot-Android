package com.alwaystinkering.sandbot.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.alwaystinkering.sandbot.data.BotStatus
import com.alwaystinkering.sandbot.data.FileListResult
import com.alwaystinkering.sandbot.data.SandBotRepository
import com.alwaystinkering.sandbot.utils.RefreshLiveData

class BotViewModel internal constructor(
    sandBotRepository: SandBotRepository
) : ViewModel() {
    val botStatus: RefreshLiveData<BotStatus> =
        sandBotRepository.getStatus()
    val fileListResult: RefreshLiveData<FileListResult> =
        sandBotRepository.getFileListResult()
}