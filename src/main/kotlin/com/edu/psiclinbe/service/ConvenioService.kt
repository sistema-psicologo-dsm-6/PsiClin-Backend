package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.ConvenioDtoRequest
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse
import com.edu.psiclinbe.mapper.ConvenioMapper
import com.edu.psiclinbe.repository.ConvenioRepository
import org.springframework.stereotype.Service

@Service
class ConvenioService(

    private val convenioRepository: ConvenioRepository,
    private val convenioMapper: ConvenioMapper

) {

    fun listarTodos(): List<ConvenioDtoResponse> =
        convenioRepository.findAll()
            .map(convenioMapper::toResponse)

    fun listarAtivos(): List<ConvenioDtoResponse> =
        convenioRepository.findByAtivoTrue()
            .map(convenioMapper::toResponse)

    fun buscarPorId(
        id: Long
    ): ConvenioDtoResponse {
        val convenio = convenioRepository.findById(id)
            .orElseThrow {
                NoSuchElementException("Convenio não encontrado com ID: $id")
            }
        return convenioMapper.toResponse(
            convenio
        )
    }

    fun buscarPorNome(
        nome: String
    ): List<ConvenioDtoResponse> =
        convenioRepository
            .buscarPorNomeParcial(nome)
            .map(convenioMapper::toResponse)

    fun buscarPorCnpj(
        cnpj: String
    ): ConvenioDtoResponse {

        val convenio =
            convenioRepository.findByCnpj(cnpj)
                ?: throw NoSuchElementException(
                    "Convênio não encontrado com CNPJ: $cnpj"
                )

        return convenioMapper.toResponse(convenio)
    }

    fun buscarPorTelefone(
        telefone: String
    ): List<ConvenioDtoResponse> =
        convenioRepository
            .findByTelefoneContainingIgnoreCase(telefone)
            .map(convenioMapper::toResponse)

    fun salvar(
        dto: ConvenioDtoRequest
    ): ConvenioDtoResponse {

        convenioRepository.findByCnpj(dto.cnpj)?.let {
            throw IllegalArgumentException(
                "Já existe um convênio cadastrado com este CNPJ"
            )
        }

        val convenio = convenioMapper.toModel(dto)

        return convenioMapper.toResponse(
            convenioRepository.save(convenio)
        )
    }

    fun atualizar(
        id: Long,
        dto: ConvenioDtoRequest
    ): ConvenioDtoResponse {

        val convenio = convenioRepository.findById(id)
            .orElseThrow {
                NoSuchElementException("Convenio não encontrado com ID: $id")
            }
        convenioRepository.findByCnpj(dto.cnpj)?.let {
            if (it.id != id) {
                throw IllegalArgumentException(
                    "Já existe outro convênio cadastrado com este CNPJ"
                )
            }
        }

        convenio.apply {
            nome = dto.nome
            cnpj = dto.cnpj
            telefone = dto.telefone
            percentualCobertura = dto.percentualCobertura
            ativo = dto.ativo
        }

        return convenioMapper.toResponse(
            convenioRepository.save(convenio)
        )
    }

    fun inativar(
        id: Long
    ): ConvenioDtoResponse {

        val convenio = convenioRepository.findById(id)
            .orElseThrow {
                NoSuchElementException("Convenio não encontrado com ID: $id")
            }

        convenio.ativo = false

        return convenioMapper.toResponse(
            convenioRepository.save(convenio)
        )
    }

    fun deletar(id: Long) {

        if (!convenioRepository.existsById(id)) {
            throw NoSuchElementException(
                "Convênio não encontrado com id: $id"
            )
        }

        convenioRepository.deleteById(id)
    }
}