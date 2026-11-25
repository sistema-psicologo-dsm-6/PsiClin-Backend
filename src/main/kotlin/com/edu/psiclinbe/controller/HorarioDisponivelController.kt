package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.HorarioDisponivelDtoRequest
import com.edu.psiclinbe.dto.response.HorarioDisponivelDtoResponse
import com.edu.psiclinbe.service.HorarioDisponivelService
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/horarios-disponiveis")
@Tag(name = "Horários Disponíveis", description = "Disponibilidade dos psicólogos")
@SecurityRequirement(name = "bearerAuth")
class HorarioDisponivelController(
    private val service: HorarioDisponivelService
) {

    @GetMapping
    fun index(authentication: Authentication): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(
            service.listarTodos(authentication)
        )

    @GetMapping("/ativos")
    fun indexActive(): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(service.listarAtivos())

    @GetMapping("/inativos")
    fun indexInactive(
        authentication: Authentication
    ): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(service.listarInativos(authentication))

    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<HorarioDisponivelDtoResponse> =
        ResponseEntity.ok(
            service.buscarPorId(id, authentication)
        )

    @GetMapping("/por-descricao")
    fun indexByDescription(
        @RequestParam termo: String,
        authentication: Authentication
    ): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(service.buscarPorDescricao(termo, authentication))

    @GetMapping("/por-psicologo/{psicologoId}")
    fun indexByPsychologist(
        @PathVariable psicologoId: Long,
        authentication: Authentication
    ): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorPsicologo(psicologoId, authentication)
        )

    @GetMapping("/por-psicologo/{psicologoId}/dia/{diaSemana}")
    fun indexByPsychologistAndWeekday(
        @PathVariable psicologoId: Long,
        @PathVariable diaSemana: String,
        authentication: Authentication
    ): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorPsicologoEDia(
                psicologoId,
                diaSemana,
                authentication
            )
        )

    @PostMapping
    fun store(
        @Valid @RequestBody dto: HorarioDisponivelDtoRequest
    ): ResponseEntity<HorarioDisponivelDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(
                service.salvar(dto)
            )

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: HorarioDisponivelDtoRequest,
        authentication: Authentication
    ): ResponseEntity<HorarioDisponivelDtoResponse> =
        ResponseEntity.ok(
            service.atualizar(id, dto, authentication)
        )

    @DeleteMapping("/{id}")
    fun destroy(
        @PathVariable id: Long
    ): ResponseEntity<Void> {

        service.deletar(id)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/destroy/{id}")
    fun destroyPermanently(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        service.excluirPermanentemente(id)
        return ResponseEntity.noContent().build()
    }
}