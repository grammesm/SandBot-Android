package com.alwaystinkering.sandbot.repo

import android.util.Log
import androidx.core.util.Consumer
import androidx.lifecycle.MutableLiveData
import com.alwaystinkering.sandbot.api.*
import com.alwaystinkering.sandbot.model.LedState
import com.alwaystinkering.sandbot.model.pattern.*
import com.alwaystinkering.sandbot.util.RefreshLiveData
import com.alwaystinkering.sandbot.util.toInt
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.apache.commons.io.FilenameUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SandbotRepository @Inject constructor(
    private val api: SandbotService,
) {

    private val TAG: String = "SandbotRepository"
    private lateinit var lastStatus: BotStatus

    fun getStatus(): RefreshLiveData<BotStatus> {
        return RefreshLiveData { callback ->
            api.getStatus().enqueue(object : Callback<BotStatus> {
                override fun onResponse(call: Call<BotStatus>, response: Response<BotStatus>) {
                    if (response.isSuccessful) {
                        lastStatus = response.body()!!
                        callback.onDataLoaded(response.body())
                    }
                }

                override fun onFailure(call: Call<BotStatus>, t: Throwable) {
                    Log.e(TAG, "Status Failure: ${t.localizedMessage}")
                    callback.onDataLoaded(null)
                }
            })

        }
    }

    fun getFileList(): RefreshLiveData<List<AbstractPattern>> {
        return RefreshLiveData { callback ->
            api.listFiles().enqueue(object : Callback<FileListResult> {
                override fun onFailure(call: Call<FileListResult>, t: Throwable) {
                    callback.onDataLoaded(null)
                    Log.e(TAG, "File list error ${t.localizedMessage}")
                }

                override fun onResponse(
                    call: Call<FileListResult>,
                    response: Response<FileListResult>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "File List Retrieved")
                        callback.onDataLoaded(response.body()!!.sandBotFiles!!.map { createPatternFromFile(it) })
                    }
                }

            })
        }
    }

    fun getFileListResult(): RefreshLiveData<FileListResult> {
        return RefreshLiveData { callback ->
            api.listFiles().enqueue(object : Callback<FileListResult> {
                override fun onFailure(call: Call<FileListResult>, t: Throwable) {
                    Log.e(TAG, "File List Result Failure: ${t.localizedMessage}")
                }

                override fun onResponse(
                    call: Call<FileListResult>,
                    response: Response<FileListResult>
                ) {
                    if (response.isSuccessful) {
                        callback.onDataLoaded(response.body())
                    }
                }
            })
        }
    }

    fun resume() {
        api.resume().enqueue(object : Callback<CommandResult> {
            override fun onFailure(call: Call<CommandResult>, t: Throwable) {
                Log.e(TAG, "Resume Command Failure")
            }

            override fun onResponse(
                call: Call<CommandResult>,
                response: Response<CommandResult>
            ) {
                if (response.isSuccessful) {
                    //if (response.body().rslt)
                    Log.d(TAG, "Resume Command Success")
                }
            }
        })
    }

    fun pause() {
        api.pause().enqueue(object : Callback<CommandResult> {
            override fun onFailure(call: Call<CommandResult>, t: Throwable) {
                Log.e(TAG, "Pause Command Failure")
            }

            override fun onResponse(
                call: Call<CommandResult>,
                response: Response<CommandResult>
            ) {
                if (response.isSuccessful) {
                    //if (response.body().rslt)
                    Log.d(TAG, "Pause Command Success")
                }
            }
        })
    }

    fun stop() {
        api.stop().enqueue(object : Callback<CommandResult> {
            override fun onFailure(call: Call<CommandResult>, t: Throwable) {
                Log.e(TAG, "Stop Command Failure")
            }

            override fun onResponse(
                call: Call<CommandResult>,
                response: Response<CommandResult>
            ) {
                if (response.isSuccessful) {
                    //if (response.body().rslt)
                    Log.d(TAG, "Stop Command Success")
                }
            }
        })
    }

    fun home() {
        api.home().enqueue(object : Callback<CommandResult> {
            override fun onFailure(call: Call<CommandResult>, t: Throwable) {
                Log.e(TAG, "Home Command Failure")
            }

            override fun onResponse(
                call: Call<CommandResult>,
                response: Response<CommandResult>
            ) {
                if (response.isSuccessful) {
                    //if (response.body().rslt)
                    Log.d(TAG, "Home Command Success")
                }
            }
        })
    }

    fun writeLedOnOff(ledOn: Boolean) {
        writeLedState(lastStatus.ledValue!!, ledOn.toInt())
    }

    fun writeLedValue(ledValue: Int) {
        writeLedState(ledValue, lastStatus.ledOn!!)
    }

    private fun writeLedState(ledValue: Int, ledOn: Int) {
        val ledState = LedState(lastStatus)
        ledState.ledValue = ledValue
        ledState.ledOn = ledOn
        val json = ledState.ledJson().replace("\\s+".toRegex(), "")
        val body = json.toRequestBody("text/plain".toMediaTypeOrNull())
        api.setLed(body).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "LED Config Write Failure")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "LED Config Write Success")
                    getStatus()
                }
            }
        })
    }

//    fun getFileList(): MutableLiveData<List<AbstractPattern>> {
//        val fileList = MutableLiveData<List<AbstractPattern>>()
//        api.listFiles().enqueue(object : Callback<FileListResult> {
//            override fun onFailure(call: Call<FileListResult>, t: Throwable) {
//                Log.e(TAG, t.localizedMessage)
//            }
//
//            override fun onResponse(
//                call: Call<FileListResult>,
//                response: Response<FileListResult>
//            ) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "File List Retrieved")
//                    fileList.value =
//                        response.body()!!.sandBotFiles!!.map { createPatternFromFile(it) }
//                }
//            }
//
//        })
//        return fileList
//    }

    fun saveFile(pattern: AbstractPattern) {
        var contents = ""
        when (pattern.fileType) {
            FileType.SEQUENCE -> {
                contents = (pattern as SequencePattern).sequenceContents.joinToString("\n")
            }
            else -> {
                Log.d(TAG, "Save ${pattern.fileType} file type not implemented")
            }
        }
        if (contents.isEmpty()) {
            return
        }
        Log.d(TAG, "Save file: " + pattern.name + " Contents: " + contents)
        val filePart = MultipartBody.Part.createFormData(
            "file", pattern.name, RequestBody.create(
                "plain/text".toMediaTypeOrNull(), contents
            )
        )
        api.saveFile(filePart).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "File upload success: " + pattern.name)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "saveFile Error")
            }
        })
    }


    fun playFile(name: String) {
        Log.d(TAG, "Play File: $name")
        api.playFile("sd", name).enqueue(object : Callback<CommandResult> {
            override fun onFailure(call: Call<CommandResult>, t: Throwable) {
                Log.e(TAG, "playFile Error")
            }

            override fun onResponse(
                call: Call<CommandResult>,
                response: Response<CommandResult>
            ) {
                if (response.isSuccessful) {
                    //if (response.body().rslt)
                    Log.d(TAG, "Play File: $name Command Success")
                }
            }
        })
    }


    fun deleteFile(name: String, callback: Consumer<Boolean>) {
        Log.d(TAG, "Delete File: $name")
        api.deleteFile("sd", name).enqueue(object : Callback<CommandResult> {
            override fun onFailure(call: Call<CommandResult>, t: Throwable) {
                Log.e(TAG, "deleteFile Error")
                callback.accept(false)
            }

            override fun onResponse(
                call: Call<CommandResult>,
                response: Response<CommandResult>
            ) {
                if (response.isSuccessful) {
                    //if (response.body().rslt)
                    Log.d(TAG, "Delete File: $name Command Success")
                    callback.accept(true)
                }
            }
        })
    }

    fun getFile(name: String, type: FileType): MutableLiveData<AbstractPattern> {
        Log.d(TAG, "Get File: $name - $type")
        val pattern = MutableLiveData<AbstractPattern>()
        when (type) {
            FileType.PARAM -> {
                api.getParametricFile("sd", name).enqueue(object : Callback<ParametricFile> {
                    override fun onFailure(call: Call<ParametricFile>, t: Throwable) {
                        Log.e(TAG, "Get Parametric Error: ${t.localizedMessage}")
                    }

                    override fun onResponse(
                        call: Call<ParametricFile>,
                        response: Response<ParametricFile>
                    ) {
                        if (response.isSuccessful) {
                            pattern.value = ParametricPattern(
                                name,
                                response.body()!!.setup,
                                response.body()!!.loop
                            )
                        }
                    }

                })
            }
            FileType.THETA_RHO -> {
                api.getThetaRhoFile("sd", name).enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.e(TAG, "Get Theta Rho Error: ${t.localizedMessage}")
                    }

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {
                        if (response.isSuccessful) {
                            pattern.value = ThetaRhoPattern(name, response.body())
                        }
                    }

                })
            }
            FileType.SEQUENCE -> {
                api.getSequenceFile("sd", name).enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.e(TAG, "Get Sequence Error: ${t.localizedMessage}")
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            pattern.value = SequencePattern(name, response.body())
                        }
                    }

                })
            }
        }
        return pattern
    }

    fun createPatternFromFile(file: SandBotFile): AbstractPattern {
        val extension = FilenameUtils.getExtension(file.name)
        val fileType = FileType.fromExtension(extension)
        //Log.d(TAG, "SandBotFile Type: $fileType")

//        if (fileType === FileType.UNKNOWN) {
//            return false
//        }

        return when (fileType) {
            FileType.PARAM -> ParametricPattern(file.name)
//            /FileType.GCODE -> ParametricPattern(file.name)
            FileType.THETA_RHO -> ThetaRhoPattern(file.name)
            FileType.SEQUENCE -> SequencePattern(file.name)
        }
    }
}