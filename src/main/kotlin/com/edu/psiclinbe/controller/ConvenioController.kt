package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.ConvenioDtoRequest
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse
import com.edu.psiclinbe.service.ConvenioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/convenios")
class ConvenioController(
    private val service: ConvenioService
) {

    @GetMapping
    fun index(): ResponseEntity<List<ConvenioDtoResponse>> =
        ResponseEntity.ok(
            service.listarTodos()
        )

    @GetMapping("/ativos")
    fun indexActive(): ResponseEntity<List<ConvenioDtoResponse>> =
        ResponseEntity.ok(
            service.listarAtivos()
        )

    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long
    ): ResponseEntity<ConvenioDtoResponse> =
        ResponseEntity.ok(
            service.buscarPorId(id)
        )

    @GetMapping("/por-nome")
    fun indexByName(
        @RequestParam nome: String
    ): ResponseEntity<List<ConvenioDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorNome(nome)
        )

    @GetMapping("/por-cnpj")
    fun showByCnpj(
        @RequestParam cnpj: String
    ): ResponseEntity<ConvenioDtoResponse> =
        ResponseEntity.ok(
            service.buscarPorCnpj(cnpj)
        )

    @GetMapping("/por-telefone")
    fun indexByPhone(
        @RequestParam telefone: String
    ): ResponseEntity<List<ConvenioDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorTelefone(telefone)
        )

    @PostMapping
    fun store(
        @Valid @RequestBody dto: ConvenioDtoRequest
    ): ResponseEntity<ConvenioDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(
                service.salvar(dto)
            )

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: ConvenioDtoRequest
    ): ResponseEntity<ConvenioDtoResponse> =
        ResponseEntity.ok(
            service.atualizar(id, dto)
        )

    @PatchMapping("/{id}/inativar")
    fun deactivate(
        @PathVariable id: Long
    ): ResponseEntity<ConvenioDtoResponse> =
        ResponseEntity.ok(
            service.inativar(id)
        )

    @DeleteMapping("/{id}", "/destroy/{id}")
    fun destroy(
        @PathVariable id: Long
    ): ResponseEntity<Void> {

        service.deletar(id)

        return ResponseEntity.noContent().build()
    }
}