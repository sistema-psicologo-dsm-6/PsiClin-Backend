package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.AgendaDtoRequest
import com.edu.psiclinbe.dto.response.AgendaDtoResponse
import com.edu.psiclinbe.mapper.AgendaMapper
import com.edu.psiclinbe.model.Agenda
import com.edu.psiclinbe.model.BloqueioAgenda
import com.edu.psiclinbe.model.HorarioDisponivel
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.SalaAtendimento
import com.edu.psiclinbe.model.TipoAtendimento
import com.edu.psiclinbe.model.Convenio
import com.edu.psiclinbe.model.enums.AgendaStatus
import com.edu.psiclinbe.model.enums.DiaSemana
import com.edu.psiclinbe.repository.AgendaRepository
import com.edu.psiclinbe.repository.BloqueioAgendaRepository
import com.edu.psiclinbe.repository.ConvenioRepository
import com.edu.psiclinbe.repository.HorarioDisponivelRepository
import com.edu.psiclinbe.repository.PacienteRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import com.edu.psiclinbe.repository.SalaAtendimentoRepository
import com.edu.psiclinbe.repository.TipoAtendimentoRepository
import com.edu.psiclinbe.validation.ValidarDataInicioFim
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class AgendaServiceTest {

    // Mocks isolam o serviço de banco, repositórios e conversão HTTP.
    private val agendaRepository = Mockito.mock(AgendaRepository::class.java)
    private val horarioRepository = Mockito.mock(HorarioDisponivelRepository::class.java)
    private val bloqueioRepository = Mockito.mock(BloqueioAgendaRepository::class.java)
    private val pacienteRepository = Mockito.mock(PacienteRepository::class.java)
    private val psicologoRepository = Mockito.mock(PsicologoRepository::class.java)
    private val salaRepository = Mockito.mock(SalaAtendimentoRepository::class.java)
    private val tipoAtendimentoRepository = Mockito.mock(TipoAtendimentoRepository::class.java)
    private val convenioRepository = Mockito.mock(ConvenioRepository::class.java)
    private val agendaMapper = Mockito.mock(AgendaMapper::class.java)
    private val service = AgendaService(
        agendaRepository,
        horarioRepository,
        bloqueioRepository,
        pacienteRepository,
        psicologoRepository,
        salaRepository,
        tipoAtendimentoRepository,
        convenioRepository,
        agendaMapper,
        ValidarDataInicioFim()
    )

    private val paciente = Mockito.mock(Paciente::class.java)
    private val psicologo = Mockito.mock(Psicologo::class.java)
    private val sala = Mockito.mock(SalaAtendimento::class.java)
    private val tipoAtendimento = Mockito.mock(TipoAtendimento::class.java)
    private val convenio = Mockito.mock(Convenio::class.java)

    @Test
    fun `returns only slots without appointments or active blocks`() {
        val psicologoId = 12L
        val data = LocalDate.of(2026, 11, 3)
        val psicologo = Mockito.mock(Psicologo::class.java)
        val horario = HorarioDisponivel(
            psicologo = psicologo,
            diaSemana = DiaSemana.TERCA,
            horaInicio = LocalTime.of(9, 0),
            horaFim = LocalTime.of(12, 0)
        )
        val agenda = agenda(
            psicologo,
            LocalDateTime.of(data, LocalTime.of(9, 0)),
            LocalDateTime.of(data, LocalTime.of(10, 0)),
            AgendaStatus.AGENDADO
        )
        val bloqueio = BloqueioAgenda(
            psicologo = psicologo,
            dataHoraInicio = LocalDateTime.of(data, LocalTime.of(11, 0)),
            dataHoraFim = LocalDateTime.of(data, LocalTime.of(12, 0)),
            motivo = "Indisponível"
        )

        Mockito.`when`(psicologoRepository.findById(psicologoId))
            .thenReturn(Optional.of(psicologo))
        Mockito.`when`(
            horarioRepository.findByPsicologoIdAndDiaSemana(psicologoId, DiaSemana.TERCA)
        ).thenReturn(listOf(horario))
        Mockito.`when`(agendaRepository.findByPsicologoId(psicologoId))
            .thenReturn(listOf(agenda))
        Mockito.`when`(bloqueioRepository.findByPsicologoIdAndAtivoTrue(psicologoId))
            .thenReturn(listOf(bloqueio))

        assertEquals(
            listOf(LocalTime.of(10, 0)),
            service.listarHorariosDisponiveis(psicologoId, data, 60)
        )
    }

    @Test
    fun `cancelled appointment does not reserve a slot`() {
        val psicologoId = 12L
        val data = LocalDate.of(2026, 11, 3)
        val psicologo = Mockito.mock(Psicologo::class.java)
        val horario = HorarioDisponivel(
            psicologo = psicologo,
            diaSemana = DiaSemana.TERCA,
            horaInicio = LocalTime.of(9, 0),
            horaFim = LocalTime.of(10, 0)
        )
        val agendaCancelada = agenda(
            psicologo,
            LocalDateTime.of(data, LocalTime.of(9, 0)),
            LocalDateTime.of(data, LocalTime.of(10, 0)),
            AgendaStatus.CANCELADO
        )

        Mockito.`when`(psicologoRepository.findById(psicologoId))
            .thenReturn(Optional.of(psicologo))
        Mockito.`when`(
            horarioRepository.findByPsicologoIdAndDiaSemana(psicologoId, DiaSemana.TERCA)
        ).thenReturn(listOf(horario))
        Mockito.`when`(agendaRepository.findByPsicologoId(psicologoId))
            .thenReturn(listOf(agendaCancelada))
        Mockito.`when`(bloqueioRepository.findByPsicologoIdAndAtivoTrue(psicologoId))
            .thenReturn(emptyList())

        assertEquals(
            listOf(LocalTime.of(9, 0)),
            service.listarHorariosDisponiveis(psicologoId, data, 60)
        )
    }

    @Test
    fun `rejects invalid duration before querying repositories`() {
        assertFailsWith<IllegalArgumentException> {
            service.listarHorariosDisponiveis(2, LocalDate.of(2026, 11, 3), 0)
        }
        Mockito.verifyNoInteractions(psicologoRepository)
    }

    @Test
    fun `rejects availability query for unknown psychologist`() {
        Mockito.`when`(psicologoRepository.findById(2))
            .thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> {
            service.listarHorariosDisponiveis(2, LocalDate.of(2026, 11, 3), 60)
        }
    }

    @Test
    fun `returns no available times when psychologist has no weekly schedule`() {
        Mockito.`when`(psicologoRepository.findById(2))
            .thenReturn(Optional.of(psicologo))
        Mockito.`when`(
            horarioRepository.findByPsicologoIdAndDiaSemana(2, DiaSemana.TERCA)
        ).thenReturn(emptyList())
        Mockito.`when`(agendaRepository.findByPsicologoId(2)).thenReturn(emptyList())
        Mockito.`when`(bloqueioRepository.findByPsicologoIdAndAtivoTrue(2))
            .thenReturn(emptyList())

        assertEquals(
            emptyList(),
            service.listarHorariosDisponiveis(2, LocalDate.of(2026, 11, 3), 60)
        )
    }

    @Test
    fun `lists all agendas mapped to response`() {
        val agenda = agenda(
            LocalDateTime.of(2026, 11, 3, 10, 0),
            LocalDateTime.of(2026, 11, 3, 11, 0),
            AgendaStatus.AGENDADO
        )
        val response = resposta(requisicao())
        Mockito.`when`(agendaRepository.findAll()).thenReturn(listOf(agenda))
        Mockito.`when`(agendaMapper.toResponse(agenda)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodos())
    }

    @Test
    fun `finds agenda by id and maps response`() {
        val agenda = agenda(
            LocalDateTime.of(2026, 11, 3, 10, 0),
            LocalDateTime.of(2026, 11, 3, 11, 0),
            AgendaStatus.AGENDADO
        )
        val response = resposta(requisicao())
        Mockito.`when`(agendaRepository.findById(100L)).thenReturn(Optional.of(agenda))
        Mockito.`when`(agendaMapper.toResponse(agenda)).thenReturn(response)

        assertSame(response, service.buscarPorId(100L))
    }

    @Test
    fun `rejects lookup for missing agenda`() {
        Mockito.`when`(agendaRepository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.buscarPorId(404L) }
    }

    @Test
    fun `searches agendas by observations and maps responses`() {
        val agenda = agenda(
            LocalDateTime.of(2026, 11, 3, 10, 0),
            LocalDateTime.of(2026, 11, 3, 11, 0),
            AgendaStatus.AGENDADO
        )
        val response = resposta(requisicao())
        Mockito.`when`(agendaRepository.buscarPorObservacoes("retorno"))
            .thenReturn(listOf(agenda))
        Mockito.`when`(agendaMapper.toResponse(agenda)).thenReturn(response)

        assertEquals(listOf(response), service.buscarPorObservacoes("retorno"))
    }

    @Test
    fun `saves agenda when selected slot is available`() {
        val dto = requisicao()
        prepararRelacionamentos(dto)

        val agenda = agenda(dto.dataHoraInicio, dto.dataHoraFim, dto.status)
        val resposta = resposta(dto)
        Mockito.`when`(
            agendaMapper.toModel(
                dto,
                paciente,
                psicologo,
                sala,
                tipoAtendimento,
                convenio
            )
        ).thenReturn(agenda)
        Mockito.`when`(agendaRepository.save(agenda)).thenReturn(agenda)
        Mockito.`when`(agendaMapper.toResponse(agenda)).thenReturn(resposta)

        assertSame(resposta, service.salvar(dto))
    }

    @Test
    fun `rejects end before start using full date and time`() {
        val dto = requisicao(
            inicio = LocalDateTime.of(2026, 11, 4, 18, 0),
            fim = LocalDateTime.of(2026, 11, 3, 19, 0)
        )

        assertFailsWith<IllegalArgumentException> { service.salvar(dto) }
        Mockito.verifyNoInteractions(pacienteRepository, psicologoRepository)
    }

    @Test
    fun `rejects appointment outside psychologist weekly schedule`() {
        val dto = requisicao()
        prepararRelacionamentos(dto, horarios = emptyList())

        assertFailsWith<IllegalArgumentException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects appointment during active psychologist block`() {
        val dto = requisicao()
        val bloqueio = BloqueioAgenda(
            psicologo = psicologo,
            dataHoraInicio = dto.dataHoraInicio,
            dataHoraFim = dto.dataHoraFim,
            motivo = "Indisponibilidade"
        )
        prepararRelacionamentos(dto, bloqueios = listOf(bloqueio))

        assertFailsWith<IllegalArgumentException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects overlapping psychologist appointment`() {
        val dto = requisicao()
        val existente = agenda(dto.dataHoraInicio, dto.dataHoraFim, AgendaStatus.AGENDADO)
        prepararRelacionamentos(dto, agendasPsicologo = listOf(existente))

        assertFailsWith<IllegalArgumentException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects overlapping room reservation`() {
        val dto = requisicao()
        val existente = agenda(dto.dataHoraInicio, dto.dataHoraFim, AgendaStatus.CONFIRMADO)
        prepararRelacionamentos(dto, agendasSala = listOf(existente))

        assertFailsWith<IllegalArgumentException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects request when patient does not exist`() {
        val dto = requisicao()
        Mockito.`when`(pacienteRepository.findById(dto.pacienteId))
            .thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects request when psychologist does not exist`() {
        val dto = requisicao()
        prepararRelacionamentos(dto)
        Mockito.`when`(psicologoRepository.findById(dto.psicologoId))
            .thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects request when appointment type does not exist`() {
        val dto = requisicao()
        prepararRelacionamentos(dto)
        Mockito.`when`(tipoAtendimentoRepository.findById(dto.tipoAtendimentoId))
            .thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects request when room does not exist`() {
        val dto = requisicao()
        prepararRelacionamentos(dto)
        Mockito.`when`(salaRepository.findById(dto.salaId)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects request when insurance plan does not exist`() {
        val dto = requisicao()
        prepararRelacionamentos(dto)
        Mockito.`when`(convenioRepository.findById(dto.convenioId))
            .thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects appointment that ends on another day`() {
        val dto = requisicao(
            inicio = LocalDateTime.of(2026, 11, 3, 23, 30),
            fim = LocalDateTime.of(2026, 11, 4, 0, 30)
        )
        prepararRelacionamentos(dto)

        assertFailsWith<IllegalArgumentException> { service.salvar(dto) }
        Mockito.verify(agendaRepository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `updates an existing agenda and returns mapped response`() {
        val id = 100L
        val dto = requisicao(fim = LocalDateTime.of(2026, 11, 3, 11, 30))
        val existing = agenda(
            LocalDateTime.of(2026, 11, 3, 9, 0),
            LocalDateTime.of(2026, 11, 3, 10, 0),
            AgendaStatus.AGENDADO
        )
        val response = resposta(dto)
        Mockito.`when`(agendaRepository.findById(id)).thenReturn(Optional.of(existing))
        prepararRelacionamentos(
            dto,
            agendasPsicologo = listOf(existing),
            agendasSala = listOf(existing)
        )
        Mockito.`when`(agendaRepository.save(existing)).thenReturn(existing)
        Mockito.`when`(agendaMapper.toResponse(existing)).thenReturn(response)

        assertSame(response, service.atualizar(id, dto))
        assertEquals(dto.dataHoraInicio, existing.dataHoraInicio)
        assertEquals(dto.dataHoraFim, existing.dataHoraFim)
        assertEquals(dto.status, existing.status)
    }

    @Test
    fun `rejects update for missing agenda`() {
        Mockito.`when`(agendaRepository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.atualizar(404L, requisicao()) }
    }

    @Test
    fun `changes agenda status`() {
        val agenda = agenda(
            LocalDateTime.of(2026, 11, 3, 10, 0),
            LocalDateTime.of(2026, 11, 3, 11, 0),
            AgendaStatus.AGENDADO
        )
        val response = resposta(requisicao()).copy(status = AgendaStatus.CONFIRMADO)
        Mockito.`when`(agendaRepository.findById(100L)).thenReturn(Optional.of(agenda))
        Mockito.`when`(agendaRepository.save(agenda)).thenReturn(agenda)
        Mockito.`when`(agendaMapper.toResponse(agenda)).thenReturn(response)

        assertSame(response, service.atualizarStatus(100L, AgendaStatus.CONFIRMADO))
        assertEquals(AgendaStatus.CONFIRMADO, agenda.status)
    }

    @Test
    fun `rejects status change for missing agenda`() {
        Mockito.`when`(agendaRepository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> {
            service.atualizarStatus(404L, AgendaStatus.CONFIRMADO)
        }
    }

    @Test
    fun `soft deletes an agenda`() {
        val agenda = agenda(
            LocalDateTime.of(2026, 11, 3, 10, 0),
            LocalDateTime.of(2026, 11, 3, 11, 0),
            AgendaStatus.AGENDADO
        )
        Mockito.`when`(agendaRepository.findById(100L)).thenReturn(Optional.of(agenda))
        Mockito.`when`(agendaRepository.save(agenda)).thenReturn(agenda)

        service.deletar(100L)

        assertEquals(false, agenda.ativo)
        Mockito.verify(agendaRepository).save(agenda)
    }

    @Test
    fun `rejects soft delete for missing agenda`() {
        Mockito.`when`(agendaRepository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.deletar(404L) }
    }

    @Test
    fun `permanently deletes existing agenda`() {
        Mockito.`when`(agendaRepository.excluirPermanentementePorId(100L)).thenReturn(1)

        service.excluirPermanentemente(100L)

        Mockito.verify(agendaRepository).excluirPermanentementePorId(100L)
    }

    @Test
    fun `rejects permanent deletion when agenda does not exist`() {
        Mockito.`when`(agendaRepository.excluirPermanentementePorId(404L)).thenReturn(0)

        assertFailsWith<NoSuchElementException> { service.excluirPermanentemente(404L) }
    }

    private fun prepararRelacionamentos(
        dto: AgendaDtoRequest,
        horarios: List<HorarioDisponivel> = listOf(
            HorarioDisponivel(
                psicologo = psicologo,
                diaSemana = DiaSemana.TERCA,
                horaInicio = LocalTime.of(9, 0),
                horaFim = LocalTime.of(12, 0)
            )
        ),
        bloqueios: List<BloqueioAgenda> = emptyList(),
        agendasPsicologo: List<Agenda> = emptyList(),
        agendasSala: List<Agenda> = emptyList()
    ) {
        // Stubs fornecem os relacionamentos e cenários de conflito de cada teste.
        Mockito.`when`(paciente.id).thenReturn(dto.pacienteId)
        Mockito.`when`(psicologo.id).thenReturn(dto.psicologoId)
        Mockito.`when`(sala.id).thenReturn(dto.salaId)
        Mockito.`when`(tipoAtendimento.id).thenReturn(dto.tipoAtendimentoId)
        Mockito.`when`(convenio.id).thenReturn(dto.convenioId)

        Mockito.`when`(pacienteRepository.findById(dto.pacienteId))
            .thenReturn(Optional.of(paciente))
        Mockito.`when`(psicologoRepository.findById(dto.psicologoId))
            .thenReturn(Optional.of(psicologo))
        Mockito.`when`(salaRepository.findById(dto.salaId))
            .thenReturn(Optional.of(sala))
        Mockito.`when`(tipoAtendimentoRepository.findById(dto.tipoAtendimentoId))
            .thenReturn(Optional.of(tipoAtendimento))
        Mockito.`when`(convenioRepository.findById(dto.convenioId))
            .thenReturn(Optional.of(convenio))
        Mockito.`when`(
            horarioRepository.findByPsicologoIdAndDiaSemana(
                dto.psicologoId,
                DiaSemana.TERCA
            )
        ).thenReturn(horarios)
        Mockito.`when`(bloqueioRepository.findByPsicologoIdAndAtivoTrue(dto.psicologoId))
            .thenReturn(bloqueios)
        Mockito.`when`(agendaRepository.findByPsicologoId(dto.psicologoId))
            .thenReturn(agendasPsicologo)
        Mockito.`when`(agendaRepository.findBySalaId(dto.salaId))
            .thenReturn(agendasSala)
    }

    private fun requisicao(
        inicio: LocalDateTime = LocalDateTime.of(2026, 11, 3, 10, 0),
        fim: LocalDateTime = LocalDateTime.of(2026, 11, 3, 11, 0)
    ) = AgendaDtoRequest(
        pacienteId = 1,
        psicologoId = 2,
        salaId = 3,
        tipoAtendimentoId = 4,
        convenioId = 5,
        dataHoraInicio = inicio,
        dataHoraFim = fim,
        status = AgendaStatus.AGENDADO
    )

    private fun agenda(
        inicio: LocalDateTime,
        fim: LocalDateTime,
        status: AgendaStatus
    ) = Agenda(
        id = 100,
        paciente = paciente,
        psicologo = psicologo,
        sala = sala,
        tipoAtendimento = tipoAtendimento,
        convenio = convenio,
        dataHoraInicio = inicio,
        dataHoraFim = fim,
        status = status
    )

    private fun resposta(dto: AgendaDtoRequest) = AgendaDtoResponse(
        id = 100,
        pacienteId = dto.pacienteId,
        psicologoId = dto.psicologoId,
        salaId = dto.salaId,
        tipoAtendimentoId = dto.tipoAtendimentoId,
        convenioId = dto.convenioId,
        dataHoraInicio = dto.dataHoraInicio,
        dataHoraFim = dto.dataHoraFim,
        status = dto.status,
        observacoes = dto.observacoes
    )

    private fun agenda(
        psicologo: Psicologo,
        inicio: LocalDateTime,
        fim: LocalDateTime,
        status: AgendaStatus
    ) = Agenda(
        paciente = Mockito.mock(Paciente::class.java),
        psicologo = psicologo,
        sala = Mockito.mock(SalaAtendimento::class.java),
        tipoAtendimento = Mockito.mock(TipoAtendimento::class.java),
        convenio = Mockito.mock(Convenio::class.java),
        dataHoraInicio = inicio,
        dataHoraFim = fim,
        status = status
    )
}