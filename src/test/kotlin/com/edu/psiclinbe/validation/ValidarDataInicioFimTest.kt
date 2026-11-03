package com.edu.psiclinbe.validation

import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ValidarDataInicioFimTest {

    private val validator = ValidarDataInicioFim()

    @Test
    fun `rejects end before start even when clock time is later`() {
        val inicio = LocalDateTime.of(2026, 11, 4, 18, 0)
        val fim = LocalDateTime.of(2026, 11, 3, 19, 0)

        assertFailsWith<IllegalArgumentException> {
            validator.validarDataHora(inicio, fim)
        }
    }

    @Test
    fun `accepts end on a later date`() {
        val inicio = LocalDateTime.of(2026, 11, 3, 18, 0)
        val fim = LocalDateTime.of(2026, 11, 4, 9, 0)

        assertTrue(fim.isAfter(inicio))
        validator.validarDataHora(inicio, fim)
    }
}