package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.TipoAtendimentoDtoRequest
import com.edu.psiclinbe.dto.response.TipoAtendimentoDtoResponse
import com.edu.psiclinbe.mapper.TipoAtendimentoMapper
import com.edu.psiclinbe.model.TipoAtendimento
import com.edu.psiclinbe.repository.TipoAtendimentoRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class TIpoAtendimentoServiceTest {
    private val repository = Mockito.mock(TipoAtendimentoRepository::class.java)
    private val mapper = Mockito.mock(TipoAtendimentoMapper::class.java)
    private val service = TIpoAtendimentoService(repository, mapper)
    private val tipo = Mockito.mock(TipoAtendimento::class.java)
    private val response = Mockito.mock(TipoAtendimentoDtoResponse::class.java)
    private val request = TipoAtendimentoDtoRequest(
        nome = "Psicoterapia",
        descricao = "Atendimento clínico",
        duracao = "50 minutos"
    )

    @Test
    fun `lists types by all name and active state`() {
        Mockito.`when`(repository.findAll()).thenReturn(listOf(tipo))
        Mockito.`when`(repository.findByNomeContainingIgnoreCase("Psico"))
            .thenReturn(listOf(tipo))
        Mockito.`when`(repository.findByAtivoTrue()).thenReturn(listOf(tipo))
        Mockito.`when`(mapper.toResponse(tipo)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodosTiposAtendimento())
        assertEquals(listOf(response), service.buscarTipoAtendimentoPeloNome("Psico"))
        assertEquals(listOf(response), service.buscarTiposAtendimetnoAtivos())
    }

    @Test
    fun `finds type and rejects unknown id`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(tipo))
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(mapper.toResponse(tipo)).thenReturn(response)

        assertSame(response, service.buscarTipoAtendimentoPorId(1L))
        assertFailsWith<NoSuchElementException> { service.buscarTipoAtendimentoPorId(404L) }
    }

    @Test
    fun `creates type without request id`() {
        val requestWithId = request.copy(id = 90L)
        Mockito.`when`(mapper.toModel(requestWithId.copy(id = null))).thenReturn(tipo)
        Mockito.`when`(repository.save(tipo)).thenReturn(tipo)
        Mockito.`when`(mapper.toResponse(tipo)).thenReturn(response)

        assertSame(response, service.cadastrarTipoAtendimento(requestWithId))
    }

    @Test
    fun `updates and deactivates type`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(tipo))
        Mockito.`when`(repository.save(tipo)).thenReturn(tipo)
        Mockito.`when`(mapper.toResponse(tipo)).thenReturn(response)

        assertSame(response, service.atualizarTipoAtendimento(1L, request))
        Mockito.verify(tipo).nome = request.nome
        assertSame(response, service.desativarTipoAtendimento(1L))
        Mockito.verify(tipo).ativo = false
    }

    @Test
    fun `deletes type entity`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(tipo))

        service.excluirTipoAtendimetno(1L)

        Mockito.verify(repository).delete(tipo)
    }
}