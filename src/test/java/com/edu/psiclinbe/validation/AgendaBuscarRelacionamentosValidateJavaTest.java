package com.edu.psiclinbe.validation;

import com.edu.psiclinbe.dto.request.AgendaDtoRequest;
import com.edu.psiclinbe.model.Convenio;
import com.edu.psiclinbe.model.Paciente;
import com.edu.psiclinbe.model.Psicologo;
import com.edu.psiclinbe.model.SalaAtendimento;
import com.edu.psiclinbe.model.TipoAtendimento;
import com.edu.psiclinbe.model.enums.AgendaStatus;
import com.edu.psiclinbe.repository.ConvenioRepository;
import com.edu.psiclinbe.repository.PacienteRepository;
import com.edu.psiclinbe.repository.PsicologoRepository;
import com.edu.psiclinbe.repository.SalaAtendimentoRepository;
import com.edu.psiclinbe.repository.TipoAtendimentoRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgendaBuscarRelacionamentosValidateJavaTest {
    private final PacienteRepository patientRepository = mock(PacienteRepository.class);
    private final PsicologoRepository psychologistRepository = mock(PsicologoRepository.class);
    private final TipoAtendimentoRepository typeRepository = mock(TipoAtendimentoRepository.class);
    private final SalaAtendimentoRepository roomRepository = mock(SalaAtendimentoRepository.class);
    private final ConvenioRepository convenioRepository = mock(ConvenioRepository.class);
    private final AgendaBuscarRelacionamentosValidate validator =
            new AgendaBuscarRelacionamentosValidate(
                    patientRepository,
                    psychologistRepository,
                    typeRepository,
                    roomRepository,
                    convenioRepository);
    private final Paciente paciente = mock(Paciente.class);
    private final Psicologo psicologo = mock(Psicologo.class);
    private final TipoAtendimento tipo = mock(TipoAtendimento.class);
    private final SalaAtendimento sala = mock(SalaAtendimento.class);
    private final Convenio convenio = mock(Convenio.class);
    private final AgendaDtoRequest request = new AgendaDtoRequest(
            null, 1L, 2L, 3L, 4L, 5L,
            LocalDateTime.of(2026, 11, 3, 10, 0),
            LocalDateTime.of(2026, 11, 3, 11, 0), AgendaStatus.AGENDADO, "");

    @Test
    void resolvesAllRequiredRelationships() {
        stubAllRelationships();

        AgendaRelacionamentos result = validator.buscarRelacionamentos(request);

        assertSame(paciente, result.getPaciente());
        assertSame(psicologo, result.getPsicologo());
        assertSame(tipo, result.getTipoAtendimento());
        assertSame(sala, result.getSala());
        assertSame(convenio, result.getConvenio());
    }

    @Test
    void rejectsAnyMissingRelationship() {
        stubAllRelationships();
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> validator.buscarRelacionamentos(request));

        when(patientRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(psychologistRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> validator.buscarRelacionamentos(request));

        when(psychologistRepository.findById(2L)).thenReturn(Optional.of(psicologo));
        when(typeRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> validator.buscarRelacionamentos(request));

        when(typeRepository.findById(4L)).thenReturn(Optional.of(tipo));
        when(roomRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> validator.buscarRelacionamentos(request));

        when(roomRepository.findById(3L)).thenReturn(Optional.of(sala));
        when(convenioRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> validator.buscarRelacionamentos(request));
    }

    private void stubAllRelationships() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(psychologistRepository.findById(2L)).thenReturn(Optional.of(psicologo));
        when(typeRepository.findById(4L)).thenReturn(Optional.of(tipo));
        when(roomRepository.findById(3L)).thenReturn(Optional.of(sala));
        when(convenioRepository.findById(5L)).thenReturn(Optional.of(convenio));
    }
}