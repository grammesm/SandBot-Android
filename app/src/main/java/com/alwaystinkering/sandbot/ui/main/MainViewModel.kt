package com.alwaystinkering.sandbot.ui.main

import androidx.lifecycle.ViewModel
import com.alwaystinkering.sandbot.api.BotStatus
import com.alwaystinkering.sandbot.api.FileListResult
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.util.RefreshLiveData
import javax.inject.Inject

class MainViewModel @Inject constructor(repository: SandbotRepository): ViewModel() {
    var botStatus: RefreshLiveData<BotStatus> = repository.getStatus()
    val fileListResult: RefreshLiveData<FileListResult> = repository.getFileListResult()
}