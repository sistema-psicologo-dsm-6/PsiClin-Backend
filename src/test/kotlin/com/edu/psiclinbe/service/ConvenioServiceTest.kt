package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.ConvenioDtoRequest
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse
import com.edu.psiclinbe.mapper.ConvenioMapper
import com.edu.psiclinbe.model.Convenio
import com.edu.psiclinbe.repository.ConvenioRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ConvenioServiceTest {
    private val repository = Mockito.mock(ConvenioRepository::class.java)
    private val mapper = Mockito.mock(ConvenioMapper::class.java)
    private val service = ConvenioService(repository, mapper)
    private val convenio = Mockito.mock(Convenio::class.java)
    private val response = Mockito.mock(ConvenioDtoResponse::class.java)
    private val request = ConvenioDtoRequest(
        nome = "Plano Norte",
        cnpj = "12345678000199",
        telefone = "11999990000",
        percentualCobertura = 80.0
    )

    @Test
    fun `lists all active and matching agreements`() {
        Mockito.`when`(repository.findAll()).thenReturn(listOf(convenio))
        Mockito.`when`(repository.findByAtivoTrue()).thenReturn(listOf(convenio))
        Mockito.`when`(repository.buscarPorNomeParcial("Norte"))
            .thenReturn(listOf(convenio))
        Mockito.`when`(repository.findByTelefoneContainingIgnoreCase("9999"))
            .thenReturn(listOf(convenio))
        Mockito.`when`(mapper.toResponse(convenio)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodos())
        assertEquals(listOf(response), service.listarAtivos())
        assertEquals(listOf(response), service.buscarPorNome("Norte"))
        assertEquals(listOf(response), service.buscarPorTelefone("9999"))
    }

    @Test
    fun `finds agreement by id and tax number`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(convenio))
        Mockito.`when`(repository.findByCnpj(request.cnpj)).thenReturn(convenio)
        Mockito.`when`(mapper.toResponse(convenio)).thenReturn(response)

        assertSame(response, service.buscarPorId(1L))
        assertSame(response, service.buscarPorCnpj(request.cnpj))
    }

    @Test
    fun `throws when agreement lookup misses`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(repository.findByCnpj("missing")).thenReturn(null)

        assertFailsWith<NoSuchElementException> { service.buscarPorId(404L) }
        assertFailsWith<NoSuchElementException> { service.buscarPorCnpj("missing") }
    }

    @Test
    fun `creates agreement when tax number is unique`() {
        Mockito.`when`(repository.findByCnpj(request.cnpj)).thenReturn(null)
        Mockito.`when`(mapper.toModel(request)).thenReturn(convenio)
        Mockito.`when`(repository.save(convenio)).thenReturn(convenio)
        Mockito.`when`(mapper.toResponse(convenio)).thenReturn(response)

        assertSame(response, service.salvar(request))
    }

    @Test
    fun `rejects duplicate tax number on create`() {
        Mockito.`when`(repository.findByCnpj(request.cnpj)).thenReturn(convenio)

        assertFailsWith<IllegalArgumentException> { service.salvar(request) }
        Mockito.verify(repository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `updates agreement and permits its own tax number`() {
        Mockito.`when`(convenio.id).thenReturn(1L)
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(convenio))
        Mockito.`when`(repository.findByCnpj(request.cnpj)).thenReturn(convenio)
        Mockito.`when`(repository.save(convenio)).thenReturn(convenio)
        Mockito.`when`(mapper.toResponse(convenio)).thenReturn(response)

        assertSame(response, service.atualizar(1L, request))
        Mockito.verify(convenio).nome = request.nome
        Mockito.verify(convenio).cnpj = request.cnpj
    }

    @Test
    fun `rejects duplicate tax number belonging to another agreement`() {
        Mockito.`when`(convenio.id).thenReturn(2L)
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(convenio))
        Mockito.`when`(repository.findByCnpj(request.cnpj)).thenReturn(convenio)

        assertFailsWith<IllegalArgumentException> { service.atualizar(1L, request) }
    }

    @Test
    fun `rejects update for missing agreement`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.atualizar(404L, request) }
    }

    @Test
    fun `inactivates and deletes agreement`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(convenio))
        Mockito.`when`(repository.save(convenio)).thenReturn(convenio)
        Mockito.`when`(repository.existsById(1L)).thenReturn(true)
        Mockito.`when`(mapper.toResponse(convenio)).thenReturn(response)

        assertSame(response, service.inativar(1L))
        service.deletar(1L)
        Mockito.verify(convenio).ativo = false
        Mockito.verify(repository).deleteById(1L)
    }

    @Test
    fun `rejects deletion of missing agreement`() {
        Mockito.`when`(repository.existsById(404L)).thenReturn(false)

        assertFailsWith<NoSuchElementException> { service.deletar(404L) }
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.inativar(404L) }
    }
}