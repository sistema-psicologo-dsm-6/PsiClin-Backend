package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.PsicologoDtoRequest
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.mapper.PsicologoMapper
import com.edu.psiclinbe.model.enums.Role
import com.edu.psiclinbe.repository.FuncionarioRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PsicologoService(
    private val psicologoRepository: PsicologoRepository,
    private val funcionarioRepository: FuncionarioRepository,
    private val psicologoMapper: PsicologoMapper,
    private val passwordEncoder: PasswordEncoder,
) {

    fun listarTodos(): List<PsicologoDtoResponse> =
        psicologoRepository.findAll()
            .map(psicologoMapper::toResponse)

    fun listarAtivos(): List<PsicologoDtoResponse> =
        psicologoRepository.findByAtivoTrue()
            .map(psicologoMapper::toResponse)

    fun buscarPorId(id: Long): PsicologoDtoResponse =
        psicologoMapper.toResponse(
            psicologoRepository.findById(id)
                .orElseThrow {
                    NoSuchElementException(
                        "Psicólogo não encontrado com id: $id"
                    )
                }
        )

    fun buscarPorCrp(crp: String): PsicologoDtoResponse {

        val psicologo = psicologoRepository.findByCrp(crp)
            ?: throw NoSuchElementException(
                "Psicólogo não encontrado com CRP: $crp"
            )

        return psicologoMapper.toResponse(psicologo)
    }

    fun buscarPorEspecialidade(especialidade: String): List<PsicologoDtoResponse> =
        psicologoRepository.findByEspecialidadeContainingIgnoreCase(especialidade)
            .map(psicologoMapper::toResponse)

    fun salvar(dto: PsicologoDtoRequest): PsicologoDtoResponse {

        verificarDuplicidade(
            email = dto.email,
            cpf = dto.cpf,
            crp = dto.crp,
            psicologoIdIgnorar = null
        )

        val psicologo = psicologoMapper.toModel(dto)

        psicologo.senha =
            passwordEncoder.encode(dto.senha)!!

        psicologo.role = Role.PSICOLOGO

        return psicologoMapper.toResponse(
            psicologoRepository.save(psicologo)
        )
    }

    fun atualizar(
        id: Long,
        dto: PsicologoDtoRequest
    ): PsicologoDtoResponse {

        val psicologo = psicologoRepository.findById(id)
            .orElseThrow {
                NoSuchElementException(
                    "Psicólogo não encontrado com id: $id"
                )
            }

        verificarDuplicidade(
            email = dto.email,
            cpf = dto.cpf,
            crp = dto.crp,
            psicologoIdIgnorar = id
        )

        psicologo.apply {
            nome = dto.nome
            cpf = dto.cpf
            email = dto.email
            telefone = dto.telefone
            crp = dto.crp
            especialidade = dto.especialidade
        }

        return psicologoMapper.toResponse(
            psicologoRepository.save(psicologo)
        )
    }

    fun inativar(id: Long): PsicologoDtoResponse {

        val psicologo = psicologoRepository.findById(id)
            .orElseThrow {
                NoSuchElementException(
                    "Psicólogo não encontrado com id: $id"
                )
            }

        psicologo.ativo = false

        return psicologoMapper.toResponse(
            psicologoRepository.save(psicologo)
        )
    }

    fun deletar(id: Long) {

        if (!psicologoRepository.existsById(id)) {
            throw NoSuchElementException(
                "Psicólogo não encontrado com id: $id"
            )
        }

        psicologoRepository.deleteById(id)
    }

    private fun verificarDuplicidade(
        email: String,
        cpf: String,
        crp: String,
        psicologoIdIgnorar: Long?
    ) {

        val existentePorEmail = funcionarioRepository.findByEmail(email)

        if (existentePorEmail != null && existentePorEmail.id != psicologoIdIgnorar) {
            throw IllegalArgumentException(
                "Já existe um funcionário com este e-mail"
            )
        }

        val existentePorCpf = funcionarioRepository.findByCpf(cpf)

        if (existentePorCpf != null && existentePorCpf.id != psicologoIdIgnorar) {
            throw IllegalArgumentException(
                "Já existe um funcionário com este CPF"
            )
        }

        val existentePorCrp = psicologoRepository.findByCrp(crp)

        if (existentePorCrp != null && existentePorCrp.id != psicologoIdIgnorar) {
            throw IllegalArgumentException(
                "Já existe um psicólogo com este CRP"
            )
        }
    }
}