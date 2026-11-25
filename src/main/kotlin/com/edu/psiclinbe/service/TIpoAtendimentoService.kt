package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.TipoAtendimentoDtoRequest
import com.edu.psiclinbe.dto.response.TipoAtendimentoDtoResponse
import com.edu.psiclinbe.mapper.TipoAtendimentoMapper
import com.edu.psiclinbe.model.TipoAtendimento
import com.edu.psiclinbe.repository.TipoAtendimentoRepository
import org.springframework.stereotype.Service

@Service
class TIpoAtendimentoService(
    private val tipoAtendimentoRepository: TipoAtendimentoRepository,
    private val tipoAtendimentoMapper: TipoAtendimentoMapper,
) {
    fun listarTodosTiposAtendimento(): List<TipoAtendimentoDtoResponse> =
        ActiveRecordVisibility.filter(tipoAtendimentoRepository.findAll()) { it.ativo }
            .map { tipoAtendimentoMapper.toResponse(it) }

    fun buscarTipoAtendimentoPorId(id: Long): TipoAtendimentoDtoResponse =
        tipoAtendimentoMapper.toResponse(buscarEntidade(id))

    fun cadastrarTipoAtendimento(dto: TipoAtendimentoDtoRequest): TipoAtendimentoDtoResponse =
        tipoAtendimentoMapper.toResponse(
            tipoAtendimentoRepository.save(tipoAtendimentoMapper.toModel(dto.copy(id = null))),
        )

    fun atualizarTipoAtendimento(id: Long, dto: TipoAtendimentoDtoRequest): TipoAtendimentoDtoResponse {
        val tipo = buscarEntidade(id)
        tipo.nome = dto.nome
        tipo.descricao = dto.descricao
        tipo.duracao = dto.duracao
        tipo.ativo = dto.ativo
        tipo.modalidade = dto.modalidade
        return tipoAtendimentoMapper.toResponse(tipoAtendimentoRepository.save(tipo))
    }

    fun desativarTipoAtendimento(id: Long): TipoAtendimentoDtoResponse {
        val tipo = buscarEntidade(id)
        tipo.ativo = false
        return tipoAtendimentoMapper.toResponse(tipoAtendimentoRepository.save(tipo))
    }

    fun excluirTipoAtendimetno(id: Long) {
        tipoAtendimentoRepository.delete(buscarEntidade(id))
    }

    fun buscarTipoAtendimentoPeloNome(nome: String): List<TipoAtendimentoDtoResponse> =
        ActiveRecordVisibility.filter(tipoAtendimentoRepository.findByNomeContainingIgnoreCase(nome)) { it.ativo }
            .map { tipoAtendimentoMapper.toResponse(it) }

    fun buscarTiposAtendimetnoAtivos(): List<TipoAtendimentoDtoResponse> =
        tipoAtendimentoRepository.findByAtivoTrue().map { tipoAtendimentoMapper.toResponse(it) }

    private fun buscarEntidade(id: Long): TipoAtendimento =
        tipoAtendimentoRepository.findById(id)
            .orElseThrow { NoSuchElementException("Não foi possível encontrar o tipo de atendimento de id: $id") }
            .also {
                ActiveRecordVisibility.requireVisible(
                    it.ativo,
                    "Não foi possível encontrar o tipo de atendimento de id: $id"
                )
            }
}
