package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.SalaAtendimentoDtoRequest
import com.edu.psiclinbe.dto.response.SalaAtendimentoDtoResponse
import com.edu.psiclinbe.mapper.SalaAtendimentoMapper
import com.edu.psiclinbe.model.SalaAtendimento
import com.edu.psiclinbe.repository.SalaAtendimentoRepository
import org.springframework.stereotype.Service

@Service
class SalaAtendimentoService(
    private val salaRepository: SalaAtendimentoRepository,
    private val salaMapper: SalaAtendimentoMapper,
) {
    fun listarTodasAsSalas(): List<SalaAtendimentoDtoResponse> =
        salaRepository.findAll().map { salaMapper.toResponse(it) }

    fun buscarSala(id: Long): SalaAtendimentoDtoResponse = salaMapper.toResponse(buscarEntidade(id))

    fun cadastrarSala(dto: SalaAtendimentoDtoRequest): SalaAtendimentoDtoResponse =
        salaMapper.toResponse(salaRepository.save(salaMapper.toModel(dto.copy(id = null))))

    fun atualizarSala(id: Long, dto: SalaAtendimentoDtoRequest): SalaAtendimentoDtoResponse {
        val sala = buscarEntidade(id)
        sala.nome = dto.nome
        sala.capacidade = dto.capacidade
        sala.descricao = dto.descricao
        return salaMapper.toResponse(salaRepository.save(sala))
    }

    fun desativarSala(id: Long): SalaAtendimentoDtoResponse {
        val sala = buscarEntidade(id)
        sala.ativo = false
        return salaMapper.toResponse(salaRepository.save(sala))
    }

    fun excluirSala(id: Long) {
        salaRepository.delete(buscarEntidade(id))
    }

    fun buscarSalaPorNome(nome: String): List<SalaAtendimentoDtoResponse> =
        salaRepository.buscarPorNomeParcial(nome).map { salaMapper.toResponse(it) }

    fun buscarSalasAtivas(): List<SalaAtendimentoDtoResponse> =
        salaRepository.findByAtivoTrue().map { salaMapper.toResponse(it) }

    private fun buscarEntidade(id: Long): SalaAtendimento =
        salaRepository.findById(id)
            .orElseThrow { NoSuchElementException("Não foi possível encontrar a sala de id: $id") }
}