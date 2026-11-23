package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.Recepcionista
import com.edu.psiclinbe.model.enums.Role
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class PerfilMapperTest {
    @Test
    fun `returns the role for psychologist and receptionist profiles`() {
        val psicologo = Psicologo(crp = "CRP-123", especialidade = "Clínica").apply {
            id = 1L
            role = Role.PSICOLOGO
        }
        val dataCadastro = LocalDateTime.of(2026, 11, 23, 10, 30)
        val recepcionista = Recepcionista(setor = "Atendimento").apply {
            id = 2L
            role = Role.RECEPCIONISTA
            this.dataCadastro = dataCadastro
        }

        assertEquals(Role.PSICOLOGO, PsicologoMapper().toResponse(psicologo).role)
        val recepcionistaResponse = RecepcionistaMapper().toResponse(recepcionista)
        assertEquals(Role.RECEPCIONISTA, recepcionistaResponse.role)
        assertEquals(dataCadastro, recepcionistaResponse.dataCadastro)
    }
}