package com.edu.psiclinbe.service

object ActiveRecordVisibility {
    fun <T> filter(records: Collection<T>, isActive: (T) -> Boolean): List<T> =
        records.filter(isActive)

    fun requireVisible(isActive: Boolean, notFoundMessage: String) {
        if (!isActive) {
            throw NoSuchElementException(notFoundMessage)
        }
    }
}