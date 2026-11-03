package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.RecepcionistaDtoRequest
import com.edu.psiclinbe.dto.response.RecepcionistaDtoResponse
import com.edu.psiclinbe.mapper.RecepcionistaMapper
import com.edu.psiclinbe.model.Recepcionista
import com.edu.psiclinbe.model.enums.Role
import com.edu.psiclinbe.repository.RecepcionistaRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class RecepcionistaServiceTest {
    private val repository = Mockito.mock(RecepcionistaRepository::class.java)
    private val mapper = Mockito.mock(RecepcionistaMapper::class.java)
    private val encoder = Mockito.mock(PasswordEncoder::class.java)
    private val service = RecepcionistaService(repository, mapper, encoder)
    private val recepcionista = Mockito.mock(Recepcionista::class.java)
    private val response = Mockito.mock(RecepcionistaDtoResponse::class.java)
    private val request = RecepcionistaDtoRequest(
        nome = "Joana Lima",
        cpf = "12345678900",
        email = "joana@example.com",
        telefone = "11999990000",
        senha = "senha-segura",
        setor = "Recepção"
    )

    @Test
    fun `lists and searches receptionists`() {
        Mockito.`when`(repository.findAll()).thenReturn(listOf(recepcionista))
        Mockito.`when`(repository.findBySetorContainingIgnoreCase("Recepção"))
            .thenReturn(listOf(recepcionista))
        Mockito.`when`(repository.findByAtivoTrue()).thenReturn(listOf(recepcionista))
        Mockito.`when`(mapper.toResponse(recepcionista)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodos())
        assertEquals(listOf(response), service.buscarPorSetor("Recepção"))
        assertEquals(listOf(response), service.buscarRecepcionistasAtivos())
    }

    @Test
    fun `finds receptionist and rejects unknown id`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(recepcionista))
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(mapper.toResponse(recepcionista)).thenReturn(response)

        assertSame(response, service.buscarRecepcionista(1L))
        assertFailsWith<NoSuchElementException> { service.buscarRecepcionista(404L) }
    }

    @Test
    fun `encodes password and assigns receptionist role on create`() {
        Mockito.`when`(mapper.toModel(request)).thenReturn(recepcionista)
        Mockito.`when`(encoder.encode(request.senha)).thenReturn("hash")
        Mockito.`when`(repository.save(recepcionista)).thenReturn(recepcionista)
        Mockito.`when`(mapper.toResponse(recepcionista)).thenReturn(response)

        assertSame(response, service.cadastrarRecepcionista(request))
        Mockito.verify(recepcionista).senha = "hash"
        Mockito.verify(recepcionista).role = Role.RECEPCIONISTA
    }

    @Test
    fun `updates and deactivates receptionist`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(recepcionista))
        Mockito.`when`(repository.save(recepcionista)).thenReturn(recepcionista)
        Mockito.`when`(mapper.toResponse(recepcionista)).thenReturn(response)

        assertSame(response, service.atualizarRecepcionista(1L, request))
        Mockito.verify(recepcionista).setor = request.setor
        assertSame(response, service.desativarRecepcionista(1L))
        Mockito.verify(recepcionista).ativo = false
    }

    @Test
    fun `deletes receptionist entity`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(recepcionista))

        service.excluitRecepcionista(1L)

        Mockito.verify(repository).delete(recepcionista)
    }
}