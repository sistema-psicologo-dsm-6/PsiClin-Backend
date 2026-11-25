package com.edu.psiclinbe.service

import org.springframework.security.core.context.SecurityContextHolder

object ActiveRecordVisibility {
    fun canViewInactive(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication ?: return false
        return authentication.isAuthenticated &&
            authentication.authorities.any { it.authority == "ROLE_ADMIN" }
    }

    fun <T> filter(records: Collection<T>, isActive: (T) -> Boolean): List<T> =
        if (canViewInactive()) records.toList() else records.filter(isActive)

    fun requireVisible(isActive: Boolean, notFoundMessage: String) {
        if (!canViewInactive() && !isActive) {
            throw NoSuchElementException(notFoundMessage)
        }
    }
}