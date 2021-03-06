package com.alwaystinkering.sandbot.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alwaystinkering.sandbot.model.LedState
import com.alwaystinkering.sandbot.model.pattern.*
import com.alwaystinkering.sandbot.utils.RefreshLiveData
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.apache.commons.io.FilenameUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SandBotRepository private constructor(){

    private val TAG = "SandBotRepository"
    private val api: SandBotInterface = RetrofitService.createService("192.168.1.170")
    private var lastStatus: BotStatus = BotStatus()

    private fun Boolean.toInt() = if (this) 1 else 0

    fun getStatus(): RefreshLiveData<BotStatus> {

        return RefreshLiveData { callback ->
            api.getStatus().enqueue(object : Callback<BotStatus> {
                override fun onResponse(call: Call<BotStatus>, response: Response<BotStatus>) {
                    if (response.isSuccessful) {
                        lastStatus = response.body()!!
                        Log.d(TAG, "BotStatus: " + response.body())
                        callback.onDataLoaded(response.body())
                    }
                }

                override fun onFailure(call: Call<BotStatus>, t: Throwable) {
                    Log.e(TAG, "Status Failure: ${t.localizedMessage}")
                }
            })
        }
    }

//    fun getStatus(): MutableLiveData<BotStatus> {
//        val status = MutableLiveData<BotStatus>()
//        api.getStatus().enqueue(object: Callback<BotStatus> {
//            override fun onFailure(call: Call<BotStatus>, t: Throwable) {
//                Log.d(TAG, "onFailure")
//                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onResponse(call: Call<BotStatus>, response: Response<BotStatus>) {
//                Log.d(TAG, "BotStatus: " + response.body())
//                if (response.isSuccessful) {
//                    status.value = response.body()
//                } else {
//                    Log.d(TAG, response.errorBody()?.string())
//                }
//            }
//
//        })
//        return status
//    }

    fun getFileListResult(): MutableLiveData<FileListResult> {
        val fileListResult = MutableLiveData<FileListResult>()
        api.listFiles().enqueue(object : Callback<FileListResult> {
            override fun onFailure(call: Call<FileListResult>, t: Throwable) {
                Log.e(TAG, "File List Result Failure: ${t.localizedMessage}")
            }

            override fun onResponse(
                call: Call<FileListResult>,
                response: Response<FileListResult>
            ) {
                if (response.isSuccessful) {
                    fileListResult.value = response.body()
                }
            }

        })
        return fileListResult
    }

//    fun getFileList(): RefreshLiveData<List<AbstractPattern>> {
//        return RefreshLiveData { callback ->
//            api.listFiles().enqueue(object : Callback<FileListResult> {
//                override fun onResponse(call: Call<FileListResult>, response: Response<FileListResult>) {
//                    if (response.isSuccessful) {
//                        callback.onDataLoaded(response.body()!!.sandBotFiles!!.map { createPatternFromFile(it) })
//                    }
//                }
//
//                override fun onFailure(call: Call<FileListResult>, t: Throwable) {
//                    Log.e(TAG, "File List Failure: ${t.localizedMessage}")
//                }
//            })
//        }
//    }

    fun getFileList(): MutableLiveData<List<AbstractPattern>> {
        val fileList = MutableLiveData<List<AbstractPattern>>()
        api.listFiles().enqueue(object : Callback<FileListResult> {
            override fun onFailure(call: Call<FileListResult>, t: Throwable) {
                Log.e(TAG, t.localizedMessage)
            }

            override fun onResponse(
                call: Call<FileListResult>,
                response: Response<FileListResult>
            ) {
                if (response.isSuccessful) {
                    fileList.value = response.body()!!.sandBotFiles!!.map { createPatternFromFile(it) }
                }
            }

        })
        return fileList
    }

    fun getFile(name: String, type: FileType): MutableLiveData<AbstractPattern> {
        Log.d(TAG, "Get File: $name - $type")
        val pattern = MutableLiveData<AbstractPattern>()
        when (type) {
            FileType.PARAM -> {
                api.getParametricFile("sd", name).enqueue(object: Callback<ParametricFile> {
                    override fun onFailure(call: Call<ParametricFile>, t: Throwable) {
                        Log.e(TAG, "Get Parametric Error: ${t.localizedMessage}")
                    }

                    override fun onResponse(
                        call: Call<ParametricFile>,
                        response: Response<ParametricFile>
                    ) {
                        if (response.isSuccessful) {
                            pattern.value = ParametricPattern(name, response.body()!!.setup, response.body()!!.loop)
                        }
                    }

                })
            }
            FileType.THETA_RHO -> {
                api.getThetaRhoFile("sd", name).enqueue(object: Callback<String> {
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
            else -> TODO()
        }
        return pattern
    }

    fun createPatternFromFile(file: SandBotFile): AbstractPattern {
        val extension = FilenameUtils.getExtension(file.name)
        val fileType = FileType.fromExtension(extension)
        Log.d(TAG, "SandBotFile Type: $fileType")

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



    fun writeLedOnOff(ledOn: Boolean) {
        writeLedState(lastStatus.ledValue!!, ledOn.toInt())
    }

    fun writeLedValue(ledValue: Int) {
        writeLedState(ledValue, lastStatus.ledOn!!)
    }

    private fun writeLedState(ledValue: Int, ledOn: Int){
        val ledState = LedState(lastStatus)
        ledState.ledValue = ledValue
        ledState.ledOn = ledOn
        val json = ledState.ledJson().replace("\\s+".toRegex(), "")
        val body = RequestBody.create(MediaType.parse("text/plain"), json)
        api.setLed(body).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "LED Config Write Success")
                }
            }
        })
    }

    fun playFile(name: String) {
        Log.d(TAG, "Play File: $name")
        api.playFile("sd", name).enqueue(object : Callback<CommandResult> {
            override fun onFailure(call: Call<CommandResult>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<CommandResult>, response: Response<CommandResult>) {
                if (response.isSuccessful) {
                    //if (response.body().rslt)
                    Log.d(TAG, "Play File: $name Command Success")
                }
            }
        })
    }

    fun resume() {
        api.resume().enqueue(object : Callback<CommandResult> {
            override fun onFailure(call: Call<CommandResult>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<CommandResult>, response: Response<CommandResult>) {
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
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<CommandResult>, response: Response<CommandResult>) {
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
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<CommandResult>, response: Response<CommandResult>) {
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
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<CommandResult>, response: Response<CommandResult>) {
                if (response.isSuccessful) {
                    //if (response.body().rslt)
                    Log.d(TAG, "Home Command Success")
                }
            }
        })
    }


    companion object {

        // For Singleton instantiation
        @Volatile private var instance: SandBotRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SandBotRepository().also { instance = it }
            }
    }
}