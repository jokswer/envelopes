package ru.akbirov.envelopes2.model

import android.content.Context
import ru.akbirov.envelopes2.sql.DBService
import java.util.*
import kotlin.collections.ArrayList

typealias EnvelopesListener = (envelopes: List<Envelope>) -> Unit

class EnvelopeService(context: Context) {
    private val dbService = DBService(context)

    private var sum = 0F

    private var envelopes: List<Envelope>

    private val listeners = mutableSetOf<EnvelopesListener>()

    init {
        envelopes = dbService.getEnvelopes()
    }

    fun getEnvelopes() = envelopes

    fun deleteEnvelope(position: Int) {
        dbService.deleteEnvelope(envelopes[position].id)
        envelopes = dbService.getEnvelopes()
        notifyChanges()
    }

    fun createEnvelope(name: String, percentage: Int) {
        dbService.createEnvelope(name, percentage)
        envelopes = dbService.getEnvelopes()
        notifyChanges()
    }

    fun changeEnvelope(name: String, percentage: Int, id: Int) {
        dbService.changeEnvelope(name, percentage, id)
        envelopes = dbService.getEnvelopes()
        notifyChanges()
    }

    fun addListener(listener: EnvelopesListener) {
        listeners.add(listener)
        listener.invoke(envelopes)
    }

    fun removeListener(listener: EnvelopesListener) {
        listeners.remove(listener)
    }

    fun onSumChange(sum: Float) {
        this.sum = sum

        envelopes = envelopes.map { it.copy(piece = getPiece(it.percentage)) }

        notifyChanges()
    }

    private fun getPiece(percentage: Int): Float {
        return sum * percentage / 100
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(envelopes) }
    }
}