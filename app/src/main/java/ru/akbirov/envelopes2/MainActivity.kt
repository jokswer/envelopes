package ru.akbirov.envelopes2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import ru.akbirov.envelopes2.databinding.ActivityMainBinding
import ru.akbirov.envelopes2.model.Envelope
import ru.akbirov.envelopes2.model.EnvelopeService
import ru.akbirov.envelopes2.model.EnvelopesListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: EnvelopesAdapter

    private val envelopeService: EnvelopeService
        get() = (applicationContext as App).envelopeService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addBtn.setOnClickListener {
            EnvelopeDialogFragment.show(supportFragmentManager, KEY_CREATE_REQUEST, "Create")
        }
        binding.sumTextInput.addTextChangedListener {
            if (it?.isNotEmpty() == true) envelopeService.onSumChange(
                it.toString().toFloat()
            ) else envelopeService.onSumChange(0F)
        }

        adapter = EnvelopesAdapter(
            object : EnvelopActionListener {
                override fun onEnvelopeChange(envelope: Envelope) {
                    EnvelopeDialogFragment.show(
                        supportFragmentManager,
                        KEY_CHANGE_REQUEST,
                        envelope.name,
                        envelope.percentage.toString(),
                        envelope.id,
                        "Change"
                    )
                }

                override fun onEnvelopeDelete(position: Int) {
                    envelopeService.deleteEnvelope(position)
                }
            }
        )
        adapter.envelopes = envelopeService.getEnvelopes()

        initRecyclerView()

        envelopeService.addListener(envelopesListener)

        setupEnvelopeDialogFragmentListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        envelopeService.removeListener(envelopesListener)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)

        with(binding.recyclerView) {
            this.layoutManager = layoutManager
            this.adapter = this@MainActivity.adapter

            addItemDecoration(
                DividerItemDecoration(
                    binding.recyclerView.context,
                    layoutManager.orientation
                )
            )
        }

        ItemTouchHelper(SwipeToDeleteCallback(adapter)).attachToRecyclerView(binding.recyclerView)

    }


    private val envelopesListener: EnvelopesListener = {
        adapter.envelopes = it
    }

    private fun setupEnvelopeDialogFragmentListeners() {
        val listener: EnvelopeDialogListener = { requestKey, name, percentage, id ->
            when (requestKey) {
                KEY_CREATE_REQUEST -> envelopeService.createEnvelope(name, percentage)
                KEY_CHANGE_REQUEST -> envelopeService.changeEnvelope(name, percentage, id!!)
            }
        }

        EnvelopeDialogFragment.setupListener(
            supportFragmentManager,
            this,
            KEY_CREATE_REQUEST,
            listener
        )
        EnvelopeDialogFragment.setupListener(
            supportFragmentManager,
            this,
            KEY_CHANGE_REQUEST,
            listener
        )
    }

    companion object {
        @JvmStatic
        private val KEY_CREATE_REQUEST = "KEY_CREATE_REQUEST"

        @JvmStatic
        private val KEY_CHANGE_REQUEST = "KEY_CHANGE_REQUEST"
    }
}