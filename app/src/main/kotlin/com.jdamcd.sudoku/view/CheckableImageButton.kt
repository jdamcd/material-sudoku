package com.jdamcd.sudoku.view

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageButton

class CheckableImageButton : AppCompatImageButton, Checkable {

    private var isChecked = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun isChecked() = isChecked

    override fun setChecked(b: Boolean) {
        if (b != isChecked) {
            isChecked = b
            refreshDrawableState()
        }
    }

    override fun toggle() {
        isChecked = !isChecked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, checkedStateSet)
        }
        return drawableState
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        invalidate()
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val saveState = state as SavedState
        super.onRestoreInstanceState(saveState.superState)
        setChecked(saveState.checked)
        isEnabled = saveState.enabled
    }

    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val saveState = SavedState(superState)
        saveState.checked = isChecked()
        saveState.enabled = isEnabled
        return saveState
    }

    private class SavedState : BaseSavedState {
        var checked: Boolean = false
        var enabled: Boolean = false

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            val values = booleanArrayOf(false, false)
            parcel.readBooleanArray(values)
            checked = values[0]
            enabled = values[1]
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeBooleanArray(booleanArrayOf(checked, enabled))
        }

        companion object {
            @JvmField @Suppress("unused")
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(parcel: Parcel): SavedState {
                    return SavedState(parcel)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private val checkedStateSet = intArrayOf(android.R.attr.state_checked)
    }
}
