package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.PsicologoDtoRequest
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.service.PsicologoService
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
@RequestMapping("/psicologos")
class PsicologoController(
    private val psicologoService: PsicologoService,
) {
    @GetMapping
    fun index(): ResponseEntity<List<PsicologoDtoResponse>> =
        ResponseEntity.ok(psicologoService.listarTodos())

    @GetMapping("/ativos")
    fun indexActive(): ResponseEntity<List<PsicologoDtoResponse>> =
        ResponseEntity.ok(psicologoService.listarAtivos())

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long): ResponseEntity<PsicologoDtoResponse> =
        ResponseEntity.ok(psicologoService.buscarPorId(id))

    @GetMapping("/por-crp")
    fun showByCrp(@RequestParam crp: String): ResponseEntity<PsicologoDtoResponse> =
        ResponseEntity.ok(psicologoService.buscarPorCrp(crp))

    @GetMapping("/por-especialidade")
    fun indexBySpecialty(
        @RequestParam especialidade: String,
    ): ResponseEntity<List<PsicologoDtoResponse>> =
        ResponseEntity.ok(psicologoService.buscarPorEspecialidade(especialidade))

    @PostMapping
    fun store(
        @Valid @RequestBody dto: PsicologoDtoRequest,
    ): ResponseEntity<PsicologoDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(psicologoService.salvar(dto))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: PsicologoDtoRequest,
    ): ResponseEntity<PsicologoDtoResponse> =
        ResponseEntity.ok(psicologoService.atualizar(id, dto))

    @PatchMapping("/{id}/inativar")
    fun deactivate(@PathVariable id: Long): ResponseEntity<PsicologoDtoResponse> =
        ResponseEntity.ok(psicologoService.inativar(id))

    @DeleteMapping("/{id}")
    fun destroy(@PathVariable id: Long): ResponseEntity<Void> {
        psicologoService.deletar(id)
        return ResponseEntity.noContent().build()
    }
}