package com.edu.psiclinbe.model

import com.edu.psiclinbe.model.enums.AgendaStatus
import jakarta.validation.Validation
import kotlin.test.Test
import kotlin.test.assertEquals
import java.time.LocalDateTime
import org.mockito.Mockito

class AgendaEntityTest {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `accepts observations within the entity limit`() {
        val violations = validator.validate(agenda("x".repeat(2000)))

        assertEquals(emptySet(), violations)
    }

    @Test
    fun `rejects observations over the entity limit`() {
        val violations = validator.validate(agenda("x".repeat(2001)))

        assertEquals(
            setOf("observacoes"),
            violations.map { it.propertyPath.toString() }.toSet()
        )
    }

    private fun agenda(observacoes: String) = Agenda(
        paciente = Mockito.mock(Paciente::class.java),
        psicologo = Mockito.mock(Psicologo::class.java),
        sala = Mockito.mock(SalaAtendimento::class.java),
        tipoAtendimento = Mockito.mock(TipoAtendimento::class.java),
        convenio = Mockito.mock(Convenio::class.java),
        dataHoraInicio = LocalDateTime.of(2026, 11, 3, 10, 0),
        dataHoraFim = LocalDateTime.of(2026, 11, 3, 11, 0),
        status = AgendaStatus.AGENDADO,
        observacoes = observacoes
    )
}