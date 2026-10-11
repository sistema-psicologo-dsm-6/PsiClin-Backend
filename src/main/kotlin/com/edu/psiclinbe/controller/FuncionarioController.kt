package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.FuncionarioDtoRequest
import com.edu.psiclinbe.dto.response.FuncionarioDtoResponse
import com.edu.psiclinbe.service.FuncionarioService
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
@RequestMapping("/funcionarios")
class FuncionarioController(
    private val funcionarioService: FuncionarioService,
) {
    @GetMapping
    fun index(): ResponseEntity<List<FuncionarioDtoResponse>> =
        ResponseEntity.ok(funcionarioService.listarTodos())

    @GetMapping("/ativos")
    fun indexActive(): ResponseEntity<List<FuncionarioDtoResponse>> =
        ResponseEntity.ok(funcionarioService.listarAtivos())

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long): ResponseEntity<FuncionarioDtoResponse> =
        ResponseEntity.ok(funcionarioService.buscarPorId(id))

    @GetMapping("/por-nome")
    fun indexByName(@RequestParam nome: String): ResponseEntity<List<FuncionarioDtoResponse>> =
        ResponseEntity.ok(funcionarioService.buscarPorNome(nome))

    @GetMapping("/por-email")
    fun showByEmail(@RequestParam email: String): ResponseEntity<FuncionarioDtoResponse> =
        ResponseEntity.ok(funcionarioService.buscarPorEmail(email))

    @GetMapping("/por-cpf")
    fun showByCpf(@RequestParam cpf: String): ResponseEntity<FuncionarioDtoResponse> =
        ResponseEntity.ok(funcionarioService.buscarPorCpf(cpf))

    @PostMapping
    fun store(
        @Valid @RequestBody dto: FuncionarioDtoRequest,
    ): ResponseEntity<FuncionarioDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(funcionarioService.salvar(dto))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: FuncionarioDtoRequest,
    ): ResponseEntity<FuncionarioDtoResponse> =
        ResponseEntity.ok(funcionarioService.atualizar(id, dto))

    @PatchMapping("/{id}/inativar")
    fun deactivate(@PathVariable id: Long): ResponseEntity<FuncionarioDtoResponse> =
        ResponseEntity.ok(funcionarioService.inativar(id))

    @DeleteMapping("/{id}", "/destroy/{id}")
    fun destroy(@PathVariable id: Long): ResponseEntity<Void> {
        funcionarioService.deletar(id)
        return ResponseEntity.noContent().build()
    }
}