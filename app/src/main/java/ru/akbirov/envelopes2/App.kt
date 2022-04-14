package ru.akbirov.envelopes2

import android.app.Application
import ru.akbirov.envelopes2.model.EnvelopeService
import ru.akbirov.envelopes2.sql.DBService

class App : Application() {
    lateinit var envelopeService: EnvelopeService

    override fun onCreate() {
        super.onCreate()
        envelopeService = EnvelopeService(applicationContext)
    }
}