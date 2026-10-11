package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.SalaAtendimentoDtoRequest
import com.edu.psiclinbe.dto.response.SalaAtendimentoDtoResponse
import com.edu.psiclinbe.service.SalaAtendimentoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/salas-atendimento")
class SalaAtendimentoController(
    private val salaAtendimentoService: SalaAtendimentoService,
) {
    @GetMapping
    fun index(): ResponseEntity<List<SalaAtendimentoDtoResponse>> =
        ResponseEntity.ok(salaAtendimentoService.listarTodasAsSalas())

    @GetMapping("/ativos")
    fun indexActive(): ResponseEntity<List<SalaAtendimentoDtoResponse>> =
        ResponseEntity.ok(salaAtendimentoService.buscarSalasAtivas())

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long): ResponseEntity<SalaAtendimentoDtoResponse> =
        ResponseEntity.ok(salaAtendimentoService.buscarSala(id))

    @GetMapping("/por-nome")
    fun indexByName(@RequestParam nome: String): ResponseEntity<List<SalaAtendimentoDtoResponse>> =
        ResponseEntity.ok(salaAtendimentoService.buscarSalaPorNome(nome))

    @PostMapping
    fun store(
        @Valid @RequestBody dto: SalaAtendimentoDtoRequest,
    ): ResponseEntity<SalaAtendimentoDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(salaAtendimentoService.cadastrarSala(dto))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: SalaAtendimentoDtoRequest,
    ): ResponseEntity<SalaAtendimentoDtoResponse> =
        ResponseEntity.ok(salaAtendimentoService.atualizarSala(id, dto))

    @PatchMapping("/{id}/inativar")
    fun deactivate(@PathVariable id: Long): ResponseEntity<SalaAtendimentoDtoResponse> =
        ResponseEntity.ok(salaAtendimentoService.desativarSala(id))

    @DeleteMapping("/{id}", "/destroy/{id}")
    fun destroy(@PathVariable id: Long): ResponseEntity<Void> {
        salaAtendimentoService.excluirSala(id)
        return ResponseEntity.noContent().build()
    }
}