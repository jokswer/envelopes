package ru.akbirov.envelopes2

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import ru.akbirov.envelopes2.databinding.FragmentEnvelopeBinding
import ru.akbirov.envelopes2.model.Envelope
import java.lang.IllegalStateException

typealias EnvelopeDialogListener = (requestKey: String, name: String, percentage: Int, id: Int?) -> Unit

class EnvelopeDialogFragment : AppCompatDialogFragment() {
    private val title: String?
        get() = requireArguments().getString(ARG_TITLE)
    private val name: String?
        get() = requireArguments().getString(ARG_NAME)
    private val percentage: String?
        get() = requireArguments().getString(ARG_PERCENTAGE)
    private val id: Int?
        get() = requireArguments().getInt(ARG_ID)
    private val requestKey: String
        get() = requireArguments().getString(ARG_REQUEST_KEY)!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBinding = FragmentEnvelopeBinding.inflate(layoutInflater)
        if (name != null) dialogBinding.textInputName.setText(name.toString())
        if (percentage != null) dialogBinding.textInputPercentage.setText(percentage.toString())

        val dialog = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder
                .setTitle(title)
                .setView(dialogBinding.root)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val enteredName = dialogBinding.textInputName.text.toString()
                val enteredPercentage = dialogBinding.textInputPercentage.text.toString()

                if (enteredName.isBlank() || enteredPercentage.isBlank()) {
                    return@setOnClickListener
                }

                val percentage = enteredPercentage.toIntOrNull()
                if (percentage == null || percentage > 100 || percentage < 0) {
                    return@setOnClickListener
                }

                parentFragmentManager.setFragmentResult(
                    requestKey, bundleOf(
                        KEY_NAME_RESPONSE to enteredName,
                        KEY_PERCENTAGE_RESPONSE to percentage,
                        KEY_ID_RESPONSE to id,
                    )
                )
                dismiss()
            }
        }

        return dialog
    }

    companion object {
        @JvmStatic
        val TAG = EnvelopeDialogFragment::class.java.simpleName

        @JvmStatic
        private val KEY_NAME_RESPONSE = "KEY_NAME_RESPONSE"

        @JvmStatic
        private val KEY_PERCENTAGE_RESPONSE = "KEY_PERCENTAGE_RESPONSE"


        @JvmStatic
        private val KEY_ID_RESPONSE = "KEY_ID_RESPONSE"

        @JvmStatic
        private val ARG_NAME = "ARG_NAME"

        @JvmStatic
        private val ARG_PERCENTAGE = "ARG_PERCENTAGE"

        @JvmStatic
        private val ARG_TITLE = "ARG_TITLE"

        @JvmStatic
        private val ARG_ID = "ARG_ID"

        @JvmStatic
        private val ARG_REQUEST_KEY = "ARG_REQUEST_KEY"

        fun show(
            manager: FragmentManager,
            requestKey: String,
            name: String?,
            percentage: String?,
            id: Int?,
            title: String?
        ) {
            val dialogFragment = EnvelopeDialogFragment()
            dialogFragment.arguments = bundleOf(
                ARG_TITLE to title,
                ARG_NAME to name,
                ARG_PERCENTAGE to percentage,
                ARG_ID to id,
                ARG_REQUEST_KEY to requestKey
            )

            dialogFragment.show(manager, TAG)
        }

        fun show(
            manager: FragmentManager,
            requestKey: String,
            title: String?
        ) {
            val dialogFragment = EnvelopeDialogFragment()
            dialogFragment.arguments = bundleOf(
                ARG_TITLE to title,
                ARG_REQUEST_KEY to requestKey
            )

            dialogFragment.show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            requestKey: String,
            listener: EnvelopeDialogListener
        ) {
            manager.setFragmentResultListener(
                requestKey,
                lifecycleOwner,
                FragmentResultListener { key, result ->
                    listener.invoke(
                        key, result.getString(KEY_NAME_RESPONSE)!!, result.getInt(
                            KEY_PERCENTAGE_RESPONSE
                        ),
                        result.getInt(KEY_ID_RESPONSE)
                    )
                })

        }
    }
}