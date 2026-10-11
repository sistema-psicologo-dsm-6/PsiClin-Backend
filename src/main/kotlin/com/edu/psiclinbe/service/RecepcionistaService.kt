package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.RecepcionistaDtoRequest
import com.edu.psiclinbe.dto.response.RecepcionistaDtoResponse
import com.edu.psiclinbe.mapper.RecepcionistaMapper
import com.edu.psiclinbe.model.Recepcionista
import com.edu.psiclinbe.repository.RecepcionistaRepository
import org.springframework.stereotype.Service

@Service
class RecepcionistaService(
    private val recepcionistaRepository: RecepcionistaRepository,
    private val recepcionistaMapper: RecepcionistaMapper,
) {
    fun listarTodos(): List<RecepcionistaDtoResponse> =
        recepcionistaRepository.findAll().map { recepcionistaMapper.toResponse(it) }

    fun buscarRecepcionista(id: Long): RecepcionistaDtoResponse =
        recepcionistaMapper.toResponse(buscarEntidade(id))

    fun cadastrarRecepcionista(dto: RecepcionistaDtoRequest): RecepcionistaDtoResponse {
        val salvo = recepcionistaRepository.save(recepcionistaMapper.toModel(dto.copy(id = null)))
        return recepcionistaMapper.toResponse(salvo)
    }

    fun atualizarRecepcionista(id: Long, dto: RecepcionistaDtoRequest): RecepcionistaDtoResponse {
        val recepcionista = buscarEntidade(id)
        recepcionista.setor = dto.setor
        recepcionista.telefone = dto.telefone
        recepcionista.nome = dto.nome
        recepcionista.email = dto.email
        recepcionista.cpf = dto.cpf
        return recepcionistaMapper.toResponse(recepcionistaRepository.save(recepcionista))
    }

    fun desativarRecepcionista(id: Long): RecepcionistaDtoResponse {
        val recepcionista = buscarEntidade(id)
        recepcionista.ativo = false
        return recepcionistaMapper.toResponse(recepcionistaRepository.save(recepcionista))
    }

    fun excluitRecepcionista(id: Long) {
        recepcionistaRepository.delete(buscarEntidade(id))
    }

    fun buscarPorSetor(setor: String): List<RecepcionistaDtoResponse> =
        recepcionistaRepository.findBySetorContainingIgnoreCase(setor).map { recepcionistaMapper.toResponse(it) }

    fun buscarRecepcionistasAtivos(): List<RecepcionistaDtoResponse> =
        recepcionistaRepository.findByAtivoTrue().map { recepcionistaMapper.toResponse(it) }

    private fun buscarEntidade(id: Long): Recepcionista =
        recepcionistaRepository.findById(id)
            .orElseThrow { NoSuchElementException("Recepcionista $id não existe") }
}
