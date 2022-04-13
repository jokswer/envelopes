package ru.akbirov.envelopes2

import android.app.Application
import ru.akbirov.envelopes2.model.EnvelopeService

class App : Application() {
    val envelopeService = EnvelopeService()
}