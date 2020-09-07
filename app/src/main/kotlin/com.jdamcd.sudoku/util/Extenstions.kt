package com.jdamcd.sudoku.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import java.util.Random

fun <E> List<E>.randomElement() = this[Random().nextInt(this.size)]

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun View.snackbar(stringId: Int) {
    Snackbar.make(this, stringId, Snackbar.LENGTH_SHORT).show()
}
