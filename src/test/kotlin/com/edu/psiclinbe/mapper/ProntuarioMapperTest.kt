package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.ProntuarioDtoRequest
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.enums.ProntuarioStatus
import org.mockito.Mockito
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertFalse

class ProntuarioMapperTest {
    private val mapper = ProntuarioMapper()

    @Test
    fun `maps inactive state from request`() {
        val request = ProntuarioDtoRequest(
            pacienteId = 1L,
            dataAbertura = LocalDateTime.of(2026, 11, 3, 9, 0),
            queixaPrincipal = "Ansiedade",
            status = ProntuarioStatus.ATIVO,
            ativo = false,
        )

        val prontuario = mapper.toModel(request, Mockito.mock(Paciente::class.java))

        assertFalse(prontuario.ativo)
    }
}
