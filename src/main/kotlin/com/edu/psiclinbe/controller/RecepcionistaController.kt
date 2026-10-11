package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.RecepcionistaDtoRequest
import com.edu.psiclinbe.dto.response.RecepcionistaDtoResponse
import com.edu.psiclinbe.service.RecepcionistaService
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
@RequestMapping("/recepcionistas")
class RecepcionistaController(
    private val recepcionistaService: RecepcionistaService,
) {
    @GetMapping
    fun index(): ResponseEntity<List<RecepcionistaDtoResponse>> =
        ResponseEntity.ok(recepcionistaService.listarTodos())

    @GetMapping("/ativos")
    fun indexActive(): ResponseEntity<List<RecepcionistaDtoResponse>> =
        ResponseEntity.ok(recepcionistaService.buscarRecepcionistasAtivos())

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long): ResponseEntity<RecepcionistaDtoResponse> =
        ResponseEntity.ok(recepcionistaService.buscarRecepcionista(id))

    @GetMapping("/por-setor")
    fun indexBySector(@RequestParam setor: String): ResponseEntity<List<RecepcionistaDtoResponse>> =
        ResponseEntity.ok(recepcionistaService.buscarPorSetor(setor))

    @PostMapping
    fun store(
        @Valid @RequestBody dto: RecepcionistaDtoRequest,
    ): ResponseEntity<RecepcionistaDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(recepcionistaService.cadastrarRecepcionista(dto))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: RecepcionistaDtoRequest,
    ): ResponseEntity<RecepcionistaDtoResponse> =
        ResponseEntity.ok(recepcionistaService.atualizarRecepcionista(id, dto))

    @PatchMapping("/{id}/inativar")
    fun deactivate(@PathVariable id: Long): ResponseEntity<RecepcionistaDtoResponse> =
        ResponseEntity.ok(recepcionistaService.desativarRecepcionista(id))

    @DeleteMapping("/{id}")
    fun destroy(@PathVariable id: Long): ResponseEntity<Void> {
        recepcionistaService.excluitRecepcionista(id)
        return ResponseEntity.noContent().build()
    }
}