package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.SalaAtendimentoDtoRequest
import com.edu.psiclinbe.dto.response.SalaAtendimentoDtoResponse
import com.edu.psiclinbe.mapper.SalaAtendimentoMapper
import com.edu.psiclinbe.model.SalaAtendimento
import com.edu.psiclinbe.repository.SalaAtendimentoRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class SalaAtendimentoServiceTest {
    private val repository = Mockito.mock(SalaAtendimentoRepository::class.java)
    private val mapper = Mockito.mock(SalaAtendimentoMapper::class.java)
    private val service = SalaAtendimentoService(repository, mapper)
    private val sala = Mockito.mock(SalaAtendimento::class.java)
    private val response = Mockito.mock(SalaAtendimentoDtoResponse::class.java)
    private val request = SalaAtendimentoDtoRequest(
        nome = "Sala 1",
        descricao = "Atendimento individual",
        capacidade = 1
    )

    @Test
    fun `lists rooms by all name and active state`() {
        Mockito.`when`(repository.findAll()).thenReturn(listOf(sala))
        Mockito.`when`(repository.buscarPorNomeParcial("Sala"))
            .thenReturn(listOf(sala))
        Mockito.`when`(repository.findByAtivoTrue()).thenReturn(listOf(sala))
        Mockito.`when`(mapper.toResponse(sala)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodasAsSalas())
        assertEquals(listOf(response), service.buscarSalaPorNome("Sala"))
        assertEquals(listOf(response), service.buscarSalasAtivas())
    }

    @Test
    fun `finds room and rejects unknown id`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(sala))
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(mapper.toResponse(sala)).thenReturn(response)

        assertSame(response, service.buscarSala(1L))
        assertFailsWith<NoSuchElementException> { service.buscarSala(404L) }
    }

    @Test
    fun `creates room without carrying request id into the model`() {
        val requestWithId = request.copy(id = 90L)
        Mockito.`when`(mapper.toModel(requestWithId.copy(id = null))).thenReturn(sala)
        Mockito.`when`(repository.save(sala)).thenReturn(sala)
        Mockito.`when`(mapper.toResponse(sala)).thenReturn(response)

        assertSame(response, service.cadastrarSala(requestWithId))
    }

    @Test
    fun `updates room and deactivates it`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(sala))
        Mockito.`when`(repository.save(sala)).thenReturn(sala)
        Mockito.`when`(mapper.toResponse(sala)).thenReturn(response)

        assertSame(response, service.atualizarSala(1L, request))
        Mockito.verify(sala).nome = request.nome
        assertSame(response, service.desativarSala(1L))
        Mockito.verify(sala).ativo = false
    }

    @Test
    fun `deletes room entity`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(sala))

        service.excluirSala(1L)

        Mockito.verify(repository).delete(sala)
    }
}