package com.edu.psiclinbe.model

import com.edu.psiclinbe.model.enums.AgendaStatus
import com.edu.psiclinbe.model.enums.DiaSemana
import com.edu.psiclinbe.model.enums.PagamentoStatus
import com.edu.psiclinbe.model.enums.ProntuarioStatus
import com.edu.psiclinbe.model.enums.Role
import jakarta.validation.Validation
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.mockito.Mockito
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class OtherEntitiesValidationTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `convenio rejects negative coverage percentage`() {
        val entity = Convenio(
            nome = "Plano",
            cnpj = "12345678000199",
            telefone = "11999990000",
            percentualCobertura = -1.0,
            ativo = true
        )

        assertInvalid(entity, "percentualCobertura")
    }

    @Test
    fun `employee rejects malformed email`() {
        val entity = Funcionario(
            nome = "Funcionário",
            cpf = "12345678900",
            email = "email-invalido",
            telefone = "11999990000",
            senha = "hash",
            role = Role.ADMIN
        )

        assertInvalid(entity, "email")
    }

    @Test
    fun `patient rejects malformed email`() {
        val entity = Paciente(
            nome = "Paciente",
            cpf = "12345678900",
            dataNascimento = LocalDate.of(1990, 1, 1),
            email = "email-invalido",
            telefone = "11999990000",
            endereco = "Rua Central"
        )

        assertInvalid(entity, "email")
    }

    @Test
    fun `psychologist requires crp`() {
        assertInvalid(Psicologo(), "crp")
    }

    @Test
    fun `receptionist requires sector`() {
        assertInvalid(Recepcionista(), "setor")
    }

    @Test
    fun `room capacity must be positive`() {
        val entity = SalaAtendimento(
            nome = "Sala",
            descricao = "Consultório",
            capacidade = 0
        )

        assertInvalid(entity, "capacidade")
    }

    @Test
    fun `service type name must have at least two characters`() {
        val entity = TipoAtendimento(
            nome = "A",
            descricao = "Atendimento",
            duracao = "50 minutos"
        )

        assertInvalid(entity, "nome")
    }

    @Test
    fun `medical record requires a chief complaint`() {
        val entity = Prontuario(
            paciente = Mockito.mock(Paciente::class.java),
            dataAbertura = LocalDateTime.of(2026, 11, 3, 9, 0),
            queixaPrincipal = "",
            status = ProntuarioStatus.ATIVO
        )

        assertInvalid(entity, "queixaPrincipal")
    }

    @Test
    fun `payment rejects negative amounts`() {
        val entity = Pagamento(
            agenda = Mockito.mock(Agenda::class.java),
            paciente = Mockito.mock(Paciente::class.java),
            convenio = Mockito.mock(Convenio::class.java),
            valorTotal = -1.0,
            valorConvenio = 0.0,
            valorPaciente = 0.0,
            formaPagamento = "Pix",
            dataPagamento = LocalDateTime.of(2026, 11, 3, 10, 0),
            status = PagamentoStatus.PENDENTE
        )

        assertInvalid(entity, "valorTotal")
    }

    @Test
    fun `available schedule description respects its maximum size`() {
        val entity = HorarioDisponivel(
            psicologo = Mockito.mock(Psicologo::class.java),
            diaSemana = DiaSemana.TERCA,
            horaInicio = LocalTime.of(9, 0),
            horaFim = LocalTime.of(10, 0),
            descricao = "x".repeat(501)
        )

        assertInvalid(entity, "descricao")
    }

    @Test
    fun `appointment block requires a reason`() {
        val entity = BloqueioAgenda(
            psicologo = Mockito.mock(Psicologo::class.java),
            dataHoraInicio = LocalDateTime.of(2026, 11, 3, 10, 0),
            dataHoraFim = LocalDateTime.of(2026, 11, 3, 11, 0),
            motivo = ""
        )

        assertInvalid(entity, "motivo")
    }

    @Test
    fun `clinical evolution description respects its maximum size`() {
        val entity = EvolucaoClinica(
            prontuario = Mockito.mock(Prontuario::class.java),
            psicologo = Mockito.mock(Psicologo::class.java),
            agenda = Mockito.mock(Agenda::class.java),
            dataRegistro = LocalDateTime.of(2026, 11, 3, 10, 0),
            descricao = "x".repeat(5001)
        )

        assertInvalid(entity, "descricao")
    }

    private fun assertInvalid(entity: Any, property: String) {
        assertTrue(
            validator.validate(entity).any { it.propertyPath.toString() == property },
            "Expected a validation violation for $property"
        )
    }
}