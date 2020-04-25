package com.jdamcd.sudoku.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.Random

fun <E> List<E>.randomElement() = this[Random().nextInt(this.size)]

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}
