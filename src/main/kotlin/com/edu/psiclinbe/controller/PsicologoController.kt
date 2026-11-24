package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.AlterarSenhaRequest
import com.edu.psiclinbe.dto.request.PsicologoDtoRequest
import com.edu.psiclinbe.dto.request.PsicologoDtoUpdateRequest
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.service.PsicologoService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Psicólogos", description = "Cadastro e gestão de psicólogos")
@SecurityRequirement(name = "bearerAuth")
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
        @Valid @RequestBody dto: PsicologoDtoUpdateRequest,
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