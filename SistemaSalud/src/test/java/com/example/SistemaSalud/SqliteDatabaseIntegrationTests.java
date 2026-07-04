package com.example.SistemaSalud;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SqliteDatabaseIntegrationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void sqliteDatabaseIsCreatedAndSeeded() {
        Integer pacientes = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM pacientes", Integer.class);
        Integer citas = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM citas", Integer.class);
        Integer medicos = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM medicos", Integer.class);

        assertThat(pacientes).isNotNull().isGreaterThan(0);
        assertThat(citas).isNotNull().isGreaterThan(0);
        assertThat(medicos).isNotNull().isGreaterThan(0);
    }
}
