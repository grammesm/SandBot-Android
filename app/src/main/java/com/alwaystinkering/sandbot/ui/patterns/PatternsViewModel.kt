package com.alwaystinkering.sandbot.ui.patterns

import androidx.lifecycle.ViewModel
import com.alwaystinkering.sandbot.api.FileListResult
import com.alwaystinkering.sandbot.repo.SandbotRepository
import com.alwaystinkering.sandbot.util.RefreshLiveData
import javax.inject.Inject

class PatternsViewModel @Inject constructor(repository: SandbotRepository): ViewModel() {
    val fileListResult: RefreshLiveData<FileListResult> = repository.getFileListResult()
}