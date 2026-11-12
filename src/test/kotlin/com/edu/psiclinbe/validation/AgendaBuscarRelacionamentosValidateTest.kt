package com.edu.psiclinbe.validation

import com.edu.psiclinbe.dto.request.AgendaDtoRequest
import com.edu.psiclinbe.model.Convenio
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.SalaAtendimento
import com.edu.psiclinbe.model.TipoAtendimento
import com.edu.psiclinbe.model.enums.AgendaStatus
import com.edu.psiclinbe.repository.ConvenioRepository
import com.edu.psiclinbe.repository.PacienteRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import com.edu.psiclinbe.repository.SalaAtendimentoRepository
import com.edu.psiclinbe.repository.TipoAtendimentoRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class AgendaBuscarRelacionamentosValidateTest {
    private val patientRepository = Mockito.mock(PacienteRepository::class.java)
    private val psychologistRepository = Mockito.mock(PsicologoRepository::class.java)
    private val typeRepository = Mockito.mock(TipoAtendimentoRepository::class.java)
    private val roomRepository = Mockito.mock(SalaAtendimentoRepository::class.java)
    private val convenioRepository = Mockito.mock(ConvenioRepository::class.java)
    private val validator = AgendaBuscarRelacionamentosValidate(
        patientRepository,
        psychologistRepository,
        typeRepository,
        roomRepository,
        convenioRepository
    )
    private val paciente = Mockito.mock(Paciente::class.java)
    private val psicologo = Mockito.mock(Psicologo::class.java)
    private val tipo = Mockito.mock(TipoAtendimento::class.java)
    private val sala = Mockito.mock(SalaAtendimento::class.java)
    private val convenio = Mockito.mock(Convenio::class.java)
    private val request = AgendaDtoRequest(
        pacienteId = 1,
        psicologoId = 2,
        salaId = 3,
        tipoAtendimentoId = 4,
        convenioId = 5,
        dataHoraInicio = LocalDateTime.of(2026, 11, 3, 10, 0),
        dataHoraFim = LocalDateTime.of(2026, 11, 3, 11, 0),
        status = AgendaStatus.AGENDADO
    )

    private fun stubAllRelationships() {
        Mockito.`when`(patientRepository.findById(1L)).thenReturn(Optional.of(paciente))
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psicologo))
        Mockito.`when`(typeRepository.findById(4L)).thenReturn(Optional.of(tipo))
        Mockito.`when`(roomRepository.findById(3L)).thenReturn(Optional.of(sala))
        Mockito.`when`(convenioRepository.findById(5L)).thenReturn(Optional.of(convenio))
    }

    @Test
    fun `resolves every relationship from request`() {
        stubAllRelationships()

        val result = validator.buscarRelacionamentos(request)

        assertSame(paciente, result.paciente)
        assertSame(psicologo, result.psicologo)
        assertSame(tipo, result.tipoAtendimento)
        assertSame(sala, result.sala)
        assertSame(convenio, result.convenio)
    }

    @Test
    fun `rejects each missing required relationship`() {
        stubAllRelationships()
        Mockito.`when`(patientRepository.findById(1L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { validator.buscarRelacionamentos(request) }

        Mockito.`when`(patientRepository.findById(1L)).thenReturn(Optional.of(paciente))
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { validator.buscarRelacionamentos(request) }

        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psicologo))
        Mockito.`when`(typeRepository.findById(4L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { validator.buscarRelacionamentos(request) }

        Mockito.`when`(typeRepository.findById(4L)).thenReturn(Optional.of(tipo))
        Mockito.`when`(roomRepository.findById(3L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { validator.buscarRelacionamentos(request) }

        Mockito.`when`(roomRepository.findById(3L)).thenReturn(Optional.of(sala))
        Mockito.`when`(convenioRepository.findById(5L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { validator.buscarRelacionamentos(request) }
    }
}