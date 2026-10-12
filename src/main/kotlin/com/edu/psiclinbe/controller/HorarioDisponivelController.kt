package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.HorarioDisponivelDtoRequest
import com.edu.psiclinbe.dto.response.HorarioDisponivelDtoResponse
import com.edu.psiclinbe.service.HorarioDisponivelService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping

@RestController
@RequestMapping("/horarios-disponiveis")
class HorarioDisponivelController(
    private val service: HorarioDisponivelService
) {

    @GetMapping
    fun index(): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(
            service.listarTodos()
        )

    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long
    ): ResponseEntity<HorarioDisponivelDtoResponse> =
        ResponseEntity.ok(
            service.buscarPorId(id)
        )

    @GetMapping("/por-descricao")
    fun indexByDescription(
        @RequestParam termo: String
    ): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(service.buscarPorDescricao(termo))

    @GetMapping("/por-psicologo/{psicologoId}")
    fun indexByPsychologist(
        @PathVariable psicologoId: Long
    ): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorPsicologo(psicologoId)
        )

    @GetMapping("/por-psicologo/{psicologoId}/dia/{diaSemana}")
    fun indexByPsychologistAndWeekday(
        @PathVariable psicologoId: Long,
        @PathVariable diaSemana: String
    ): ResponseEntity<List<HorarioDisponivelDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorPsicologoEDia(
                psicologoId,
                diaSemana
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
        @Valid @RequestBody dto: HorarioDisponivelDtoRequest
    ): ResponseEntity<HorarioDisponivelDtoResponse> =
        ResponseEntity.ok(
            service.atualizar(id, dto)
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