package ru.akbirov.envelopes2.model

import java.util.*
import kotlin.collections.ArrayList

typealias EnvelopesListener = (envelopes: List<Envelope>) -> Unit

class EnvelopeService {

    private var sum = 0F

    private var envelopes = mutableListOf<Envelope>()

    private val listeners = mutableSetOf<EnvelopesListener>()

    init {
        envelopes = mutableListOf(
            Envelope("1", "Личные", 60, 0F),
            Envelope("2", "Общие", 30, 0F),
            Envelope("3", "Инвестиции", 10, 0F),
            Envelope("4", "Большие покупки", 10, 0F),
        )
    }

    private fun findIndexById(id: String) = envelopes.indexOfFirst { it.id == id }

    fun getEnvelopes() = envelopes

    fun deleteEnvelope(position: Int) {
        envelopes = ArrayList(envelopes)
        envelopes.removeAt(position)
        notifyChanges()
    }

    fun createEnvelope(name: String, percentage: Int) {
        envelopes.add(
            Envelope(
                UUID.randomUUID().toString(),
                name,
                percentage,
                getPiece(percentage)
            )
        )

        envelopes = ArrayList(envelopes )

        notifyChanges()
    }

    fun changeEnvelope(name: String, percentage: Int, id: String) {
        val index = findIndexById(id)

        if (index != -1) {
            val updateEnvelope = envelopes[index].copy(
                name = name,
                percentage = percentage,
                piece = getPiece(percentage)
            )
            envelopes = ArrayList(envelopes)
            envelopes[index] = updateEnvelope
            notifyChanges()
        }
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

        envelopes = envelopes.map { it.copy(piece = getPiece(it.percentage)) }.toMutableList()

        notifyChanges()
    }

    private fun getPiece(percentage: Int): Float {
        return sum * percentage / 100
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(envelopes) }
    }
}