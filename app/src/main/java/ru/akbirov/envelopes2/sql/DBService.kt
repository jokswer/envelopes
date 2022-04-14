package ru.akbirov.envelopes2.sql

import android.content.Context
import android.database.Cursor
import androidx.core.content.contentValuesOf
import ru.akbirov.envelopes2.model.Envelope

class DBService(context: Context) {
    private val database = AppSQLiteHelper(context).writableDatabase

    fun createEnvelope(name: String, percentage: Int) {
        database.insert(
            AppSQLiteConstants.TABLE_NAME, null, contentValuesOf(
                AppSQLiteConstants.COLUMN_NAME to name,
                AppSQLiteConstants.COLUMN_PERCENTAGE to percentage
            )
        )
    }

    fun getEnvelopes(): List<Envelope> {
        val cursor = database.rawQuery("SELECT * FROM ${AppSQLiteConstants.TABLE_NAME}", null)

        return cursor.use {
            val list = mutableListOf<Envelope>()

            while (cursor.moveToNext()) {
                list.add(parseEnvelope(cursor))
            }

            return@use list
        }
    }

    fun changeEnvelope(name: String, percentage: Int, envelopeId: Int) {
        database.update(
            AppSQLiteConstants.TABLE_NAME, contentValuesOf(
                AppSQLiteConstants.COLUMN_NAME to name,
                AppSQLiteConstants.COLUMN_PERCENTAGE to percentage
            ), "${AppSQLiteConstants.COLUMN_ID} = ?", arrayOf(envelopeId.toString())
        )
    }

    fun deleteEnvelope(envelopeId: Int) {
        database.delete(
            AppSQLiteConstants.TABLE_NAME,
            "${AppSQLiteConstants.COLUMN_ID} = ?",
            arrayOf(envelopeId.toString())
        )
    }

    private fun parseEnvelope(cursor: Cursor): Envelope {
        return Envelope(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLiteConstants.COLUMN_ID)),
            name = cursor.getString(cursor.getColumnIndexOrThrow(AppSQLiteConstants.COLUMN_NAME)),
            percentage = cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLiteConstants.COLUMN_PERCENTAGE)),
            piece = 0F
        )
    }
}