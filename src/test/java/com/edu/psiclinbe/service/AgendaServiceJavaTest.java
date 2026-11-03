package com.edu.psiclinbe.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.edu.psiclinbe.mapper.AgendaMapper;
import com.edu.psiclinbe.model.HorarioDisponivel;
import com.edu.psiclinbe.model.Psicologo;
import com.edu.psiclinbe.model.enums.DiaSemana;
import com.edu.psiclinbe.repository.AgendaRepository;
import com.edu.psiclinbe.repository.BloqueioAgendaRepository;
import com.edu.psiclinbe.repository.ConvenioRepository;
import com.edu.psiclinbe.repository.HorarioDisponivelRepository;
import com.edu.psiclinbe.repository.PacienteRepository;
import com.edu.psiclinbe.repository.PsicologoRepository;
import com.edu.psiclinbe.repository.SalaAtendimentoRepository;
import com.edu.psiclinbe.repository.TipoAtendimentoRepository;
import com.edu.psiclinbe.validation.ValidarDataInicioFim;

class AgendaServiceJavaTest {
    private final AgendaRepository agendaRepository = mock(AgendaRepository.class);
    private final HorarioDisponivelRepository scheduleRepository =
            mock(HorarioDisponivelRepository.class);
    private final BloqueioAgendaRepository blockRepository = mock(BloqueioAgendaRepository.class);
    private final PsicologoRepository psychologistRepository = mock(PsicologoRepository.class);
    private final AgendaService service = new AgendaService(
            agendaRepository,
            scheduleRepository,
            blockRepository,
            mock(PacienteRepository.class),
            psychologistRepository,
            mock(SalaAtendimentoRepository.class),
            mock(TipoAtendimentoRepository.class),
            mock(ConvenioRepository.class),
            mock(AgendaMapper.class),
            new ValidarDataInicioFim());

    @Test
    void returnsStartsOfFreeAppointmentSlots() {
        long psychologistId = 12L;
        LocalDate date = LocalDate.of(2026, 11, 3);
        Psicologo psychologist = mock(Psicologo.class);
        HorarioDisponivel schedule = new HorarioDisponivel(
                null, psychologist, DiaSemana.TERCA,
                LocalTime.of(9, 0), LocalTime.of(11, 0), null, true);
        when(psychologistRepository.findById(psychologistId))
                .thenReturn(Optional.of(psychologist));
        when(scheduleRepository.findByPsicologoIdAndDiaSemana(psychologistId, DiaSemana.TERCA))
                .thenReturn(List.of(schedule));
        when(agendaRepository.findByPsicologoId(psychologistId)).thenReturn(List.of());
        when(blockRepository.findByPsicologoIdAndAtivoTrue(psychologistId)).thenReturn(List.of());

        assertEquals(
                List.of(LocalTime.of(9, 0), LocalTime.of(10, 0)),
                service.listarHorariosDisponiveis(psychologistId, date, 60L));
    }

    @Test
    void rejectsZeroDurationAndUnknownPsychologist() {
        LocalDate date = LocalDate.of(2026, 11, 3);

        assertThrows(IllegalArgumentException.class,
                () -> service.listarHorariosDisponiveis(12L, date, 0L));
        when(psychologistRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> service.listarHorariosDisponiveis(404L, date, 60L));
    }
}