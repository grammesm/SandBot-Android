package com.alwaystinkering.sandbot.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// Convert boolean to int representation
fun Boolean.toInt() = if (this) 1 else 0

// inflate a view id from a ViewGroup
fun ViewGroup.inflater(layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)
