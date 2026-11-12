package com.edu.psiclinbe.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class AgendaServiceComplexGraphTest {

    private final AgendaServiceComplexGraphFixture fixture =
            new AgendaServiceComplexGraphFixture();

    @Test
    void returnsOnlySlotsWithoutAppointmentsOrBlocks() {
        LocalDate date = LocalDate.of(2026, 11, 3);
        List<AgendaServiceComplexGraphFixture.Horario> schedules = List.of(
                new AgendaServiceComplexGraphFixture.Horario(
                        LocalTime.of(9, 0), LocalTime.of(12, 0))
        );
        List<AgendaServiceComplexGraphFixture.Agendamento> appointments = List.of(
                new AgendaServiceComplexGraphFixture.Agendamento(
                        LocalDateTime.of(date, LocalTime.of(9, 0)),
                        LocalDateTime.of(date, LocalTime.of(10, 0)),
                        true)
        );
        List<AgendaServiceComplexGraphFixture.Bloqueio> blocks = List.of(
                new AgendaServiceComplexGraphFixture.Bloqueio(
                        LocalDateTime.of(date, LocalTime.of(11, 0)),
                        LocalDateTime.of(date, LocalTime.of(12, 0)))
        );

        List<LocalTime> available = fixture.listarHorariosDisponiveis(
                date, 60, true, schedules, appointments, blocks);

        assertEquals(List.of(LocalTime.of(10, 0)), available);
    }

    @Test
    void cancelledAppointmentDoesNotReserveSlot() {
        LocalDate date = LocalDate.of(2026, 11, 3);
        List<AgendaServiceComplexGraphFixture.Horario> schedules = List.of(
                new AgendaServiceComplexGraphFixture.Horario(
                        LocalTime.of(9, 0), LocalTime.of(10, 0))
        );
        List<AgendaServiceComplexGraphFixture.Agendamento> appointments = List.of(
                new AgendaServiceComplexGraphFixture.Agendamento(
                        LocalDateTime.of(date, LocalTime.of(9, 0)),
                        LocalDateTime.of(date, LocalTime.of(10, 0)),
                        false)
        );

        List<LocalTime> available = fixture.listarHorariosDisponiveis(
                date, 60, true, schedules, appointments, List.of());

        assertEquals(List.of(LocalTime.of(9, 0)), available);
    }

    @Test
    void rejectsInvalidDurationAndUnknownPsychologist() {
        LocalDate date = LocalDate.of(2026, 11, 3);

        IllegalArgumentException invalidDuration = assertThrows(IllegalArgumentException.class, () ->
                fixture.listarHorariosDisponiveis(
                        date, 0, true, List.of(), List.of(), List.of()));
        assertEquals("Duração inválida", invalidDuration.getMessage());

        NoSuchElementException missingPsychologist = assertThrows(NoSuchElementException.class, () ->
                fixture.listarHorariosDisponiveis(
                        date, 60, false, List.of(), List.of(), List.of()));
        assertEquals("Psicólogo não encontrado", missingPsychologist.getMessage());
    }
}

final class AgendaServiceComplexGraphFixture {

    public List<LocalTime> listarHorariosDisponiveis(
            LocalDate data,
            long duracaoMinutos,
            boolean psicologoExiste,
            List<Horario> horarios,
            List<Agendamento> agendas,
            List<Bloqueio> bloqueios) {
        if (duracaoMinutos <= 0) {
            throw new IllegalArgumentException("Duração inválida");
        }

        if (!psicologoExiste) {
            throw new NoSuchElementException("Psicólogo não encontrado");
        }

        Set<LocalTime> disponiveis = new TreeSet<>();

        for (Horario horario : horarios) {
            LocalDateTime inicioSlot = data.atTime(horario.horaInicio);
            LocalDateTime fimHorario = data.atTime(horario.horaFim);

            while (!inicioSlot.plusMinutes(duracaoMinutos).isAfter(fimHorario)) {
                LocalDateTime fimSlot = inicioSlot.plusMinutes(duracaoMinutos);
                boolean conflitoAgenda = false;

                for (Agendamento agenda : agendas) {
                    if (agenda.reservaHorario
                            && sobrepoe(inicioSlot, fimSlot, agenda.inicio, agenda.fim)) {
                        conflitoAgenda = true;
                        break;
                    }
                }

                boolean conflitoBloqueio = false;

                for (Bloqueio bloqueio : bloqueios) {
                    if (sobrepoe(inicioSlot, fimSlot, bloqueio.inicio, bloqueio.fim)) {
                        conflitoBloqueio = true;
                        break;
                    }
                }

                if (!conflitoAgenda && !conflitoBloqueio) {
                    disponiveis.add(inicioSlot.toLocalTime());
                }

                inicioSlot = fimSlot;
            }
        }

        return new ArrayList<>(disponiveis);
    }

    private boolean sobrepoe(
            LocalDateTime inicio,
            LocalDateTime fim,
            LocalDateTime outroInicio,
            LocalDateTime outroFim) {
        return outroInicio.isBefore(fim) && inicio.isBefore(outroFim);
    }

    static final class Horario {
        private final LocalTime horaInicio;
        private final LocalTime horaFim;

        Horario(LocalTime horaInicio, LocalTime horaFim) {
            this.horaInicio = horaInicio;
            this.horaFim = horaFim;
        }
    }

    static final class Agendamento {
        private final LocalDateTime inicio;
        private final LocalDateTime fim;
        private final boolean reservaHorario;

        Agendamento(LocalDateTime inicio, LocalDateTime fim, boolean reservaHorario) {
            this.inicio = inicio;
            this.fim = fim;
            this.reservaHorario = reservaHorario;
        }
    }

    static final class Bloqueio {
        private final LocalDateTime inicio;
        private final LocalDateTime fim;

        Bloqueio(LocalDateTime inicio, LocalDateTime fim) {
            this.inicio = inicio;
            this.fim = fim;
        }
    }
}