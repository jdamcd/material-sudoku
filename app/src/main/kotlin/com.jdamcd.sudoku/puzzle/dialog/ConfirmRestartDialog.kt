package com.jdamcd.sudoku.puzzle.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.jdamcd.sudoku.R

@SuppressLint("ValidFragment")
class ConfirmRestartDialog : DialogFragment(), OnClickListener {

    private var callback: RestartContract? = null

    interface RestartContract {
        fun onRestart()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireActivity())
                .setTitle(R.string.dialog_restart)
                .setMessage(R.string.dialog_restart_warning)
                .setNegativeButton(android.R.string.cancel, this)
                .setPositiveButton(android.R.string.yes, this)
                .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try {
            callback = parentFragmentManager.findFragmentById(R.id.fragment_puzzle) as RestartContract
        } catch (e: ClassCastException) {
            throw ClassCastException("Must implement " + RestartContract::class.java.simpleName)
        }

        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                callback?.onRestart()
                dialog.dismiss()
            }
            DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
        }
    }

    companion object {
        const val TAG = "confirm_restart"
    }
}
