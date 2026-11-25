package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.SalaAtendimentoDtoRequest
import com.edu.psiclinbe.model.SalaAtendimento
import kotlin.test.Test
import kotlin.test.assertEquals

class SalaAtendimentoMapperTest {
    private val mapper = SalaAtendimentoMapper()

    @Test
    fun `maps room resources to model and response`() {
        val recursos = listOf("Divã", "Computador")
        val request = SalaAtendimentoDtoRequest(
            nome = "Sala 1",
            descricao = "Atendimento individual",
            capacidade = 1,
            recursosDisponiveis = recursos
        )

        val sala = mapper.toModel(request)
        val response = mapper.toResponse(sala)

        assertEquals(recursos, sala.recursosDisponiveis)
        assertEquals(recursos, response.recursosDisponiveis)
    }

    @Test
    fun `keeps resource list empty for existing rooms without resources`() {
        val sala = SalaAtendimento(
            nome = "Sala antiga",
            descricao = "Sala existente",
            capacidade = 1
        )

        assertEquals(emptyList(), mapper.toResponse(sala).recursosDisponiveis)
    }
}
