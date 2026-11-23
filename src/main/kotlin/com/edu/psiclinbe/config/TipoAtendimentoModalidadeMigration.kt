package com.edu.psiclinbe.config

import jakarta.annotation.PostConstruct
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class TipoAtendimentoModalidadeMigration(
    private val jdbcTemplate: JdbcTemplate,
) {
    @PostConstruct
    fun migrate() {
        jdbcTemplate.execute(
            "ALTER TABLE tipos_atendimento ADD COLUMN IF NOT EXISTS modalidade varchar(20)"
        )
        jdbcTemplate.update(
            "UPDATE tipos_atendimento SET modalidade = 'PRESENCIAL' WHERE modalidade IS NULL"
        )
        jdbcTemplate.execute(
            "ALTER TABLE tipos_atendimento ALTER COLUMN modalidade SET DEFAULT 'PRESENCIAL'"
        )
        jdbcTemplate.execute(
            "ALTER TABLE tipos_atendimento ALTER COLUMN modalidade SET NOT NULL"
        )
    }
}
