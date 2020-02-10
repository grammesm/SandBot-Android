package com.alwaystinkering.sandbot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import com.alwaystinkering.sandbot.databinding.ActivitySandbotBinding

class SandBotActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView<ActivitySandbotBinding>(this, R.layout.activity_sandbot)
    }
}
