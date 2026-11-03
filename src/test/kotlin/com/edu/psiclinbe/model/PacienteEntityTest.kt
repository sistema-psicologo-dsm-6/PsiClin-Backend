package com.edu.psiclinbe.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PacienteEntityTest {

    @Test
    fun `patients with same persisted id are equal`() {
        val first = Paciente(id = 15L)
        val second = Paciente(id = 15L)

        assertTrue(first == second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun `patients without persisted ids are not equal`() {
        val first = Paciente()
        val second = Paciente()

        assertFalse(first == second)
        assertEquals(0, first.hashCode())
    }

    @Test
    fun `patients with different ids are not equal`() {
        assertFalse(Paciente(id = 1L) == Paciente(id = 2L))
    }
}