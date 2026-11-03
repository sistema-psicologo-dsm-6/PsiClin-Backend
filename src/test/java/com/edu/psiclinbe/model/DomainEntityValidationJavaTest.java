package com.edu.psiclinbe.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import com.edu.psiclinbe.model.enums.AgendaStatus;
import com.edu.psiclinbe.model.enums.DiaSemana;
import com.edu.psiclinbe.model.enums.PagamentoStatus;
import com.edu.psiclinbe.model.enums.ProntuarioStatus;
import com.edu.psiclinbe.model.enums.Role;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class DomainEntityValidationJavaTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void convenioRejectsNegativeCoverage() {
        Convenio entity = new Convenio(
                null, "Plano", "12345678000199", "11999990000", -1.0, true, java.util.Set.of());
        assertInvalid(entity, "percentualCobertura");
    }

    @Test
    void employeeAndPatientRejectMalformedEmail() {
        Funcionario employee = new Funcionario(
                null, "Nome", "12345678900", "email-invalido", "11999990000",
                "hash", true, LocalDateTime.now(), Role.ADMIN);
        Paciente patient = new Paciente(
                null, "Paciente", "12345678900", LocalDate.of(1990, 1, 1),
                "email-invalido", "11999990000", "Rua", "", true,
                LocalDateTime.now(), java.util.List.of(), java.util.Set.of());

        assertInvalid(employee, "email");
        assertInvalid(patient, "email");
    }

    @Test
    void psychologistAndReceptionistRequireTheirSpecificFields() {
        assertInvalid(new Psicologo("", "Clínica"), "crp");
        assertInvalid(new Recepcionista(""), "setor");
    }

    @Test
    void roomAndServiceTypeRequireValidNamesAndCapacity() {
        SalaAtendimento room = new SalaAtendimento(
                null, "Sala", "Consultório", 0, true, LocalDateTime.now());
        TipoAtendimento type = new TipoAtendimento(
                null, "A", "Atendimento", "50 min", true, LocalDateTime.now());

        assertInvalid(room, "capacidade");
        assertInvalid(type, "nome");
    }

    @Test
    void medicalRecordRequiresChiefComplaint() {
        Prontuario record = new Prontuario(
                null, mock(Paciente.class), LocalDateTime.now(), "", ProntuarioStatus.ATIVO, true);

        assertInvalid(record, "queixaPrincipal");
    }

    @Test
    void paymentRejectsNegativeAmount() {
        Pagamento payment = new Pagamento(
                null, mock(Agenda.class), mock(Paciente.class), mock(Convenio.class),
                -1.0, 0.0, 0.0, "Pix", LocalDateTime.now(), PagamentoStatus.PENDENTE);

        assertInvalid(payment, "valorTotal");
    }

    @Test
    void blockRequiresReasonAndClinicalEvolutionHasDescriptionLimit() {
        BloqueioAgenda block = new BloqueioAgenda(
                null, mock(Psicologo.class), LocalDateTime.now(), LocalDateTime.now().plusHours(1), "", true);
        EvolucaoClinica evolution = new EvolucaoClinica(
                null, mock(Prontuario.class), mock(Psicologo.class), mock(Agenda.class),
                LocalDateTime.now(), "x".repeat(5001), true);

        assertInvalid(block, "motivo");
        assertInvalid(evolution, "descricao");
    }

    @Test
    void weeklyScheduleDescriptionHasMaximumLength() {
        HorarioDisponivel schedule = new HorarioDisponivel(
                null, mock(Psicologo.class), DiaSemana.TERCA,
                LocalTime.of(9, 0), LocalTime.of(10, 0), "x".repeat(501), true);

        assertInvalid(schedule, "descricao");
    }

    @Test
    void agendaObservationsHaveMaximumLength() {
        Agenda agenda = new Agenda(
                null, mock(Paciente.class), mock(Psicologo.class), mock(SalaAtendimento.class),
                mock(TipoAtendimento.class), mock(Convenio.class),
                LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                AgendaStatus.AGENDADO, "x".repeat(2001), true);

        assertInvalid(agenda, "observacoes");
    }

    private void assertInvalid(Object entity, String property) {
        assertTrue(
                validator.validate(entity).stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals(property)),
                "Expected a validation violation for " + property);
    }
}