package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.TipoAtendimentoDtoRequest
import com.edu.psiclinbe.dto.response.TipoAtendimentoDtoResponse
import com.edu.psiclinbe.service.TIpoAtendimentoService
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
@RequestMapping("/tipos-atendimento")
class TipoAtendimentoController(
    private val tipoAtendimentoService: TIpoAtendimentoService,
) {
    @GetMapping
    fun index(): ResponseEntity<List<TipoAtendimentoDtoResponse>> =
        ResponseEntity.ok(tipoAtendimentoService.listarTodosTiposAtendimento())

    @GetMapping("/ativos")
    fun indexActive(): ResponseEntity<List<TipoAtendimentoDtoResponse>> =
        ResponseEntity.ok(tipoAtendimentoService.buscarTiposAtendimetnoAtivos())

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long): ResponseEntity<TipoAtendimentoDtoResponse> =
        ResponseEntity.ok(tipoAtendimentoService.buscarTipoAtendimentoPorId(id))

    @GetMapping("/por-nome")
    fun indexByName(@RequestParam nome: String): ResponseEntity<List<TipoAtendimentoDtoResponse>> =
        ResponseEntity.ok(tipoAtendimentoService.buscarTipoAtendimentoPeloNome(nome))

    @PostMapping
    fun store(
        @Valid @RequestBody dto: TipoAtendimentoDtoRequest,
    ): ResponseEntity<TipoAtendimentoDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(tipoAtendimentoService.cadastrarTipoAtendimento(dto))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: TipoAtendimentoDtoRequest,
    ): ResponseEntity<TipoAtendimentoDtoResponse> =
        ResponseEntity.ok(tipoAtendimentoService.atualizarTipoAtendimento(id, dto))

    @PatchMapping("/{id}/inativar")
    fun deactivate(@PathVariable id: Long): ResponseEntity<TipoAtendimentoDtoResponse> =
        ResponseEntity.ok(tipoAtendimentoService.desativarTipoAtendimento(id))

    @DeleteMapping("/{id}")
    fun destroy(@PathVariable id: Long): ResponseEntity<Void> {
        tipoAtendimentoService.excluirTipoAtendimetno(id)
        return ResponseEntity.noContent().build()
    }
}