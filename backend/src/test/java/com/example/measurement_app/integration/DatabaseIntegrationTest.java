package com.example.measurement_app.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Test that the database connection is established and accessible.
     */
    @Test
    void shouldEstablishDatabaseConnectionSuccessfully() {
        // Act: Query the database to count the number of tables
        Integer tableCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.tables", Integer.class);

        // Assert: Verify that the database contains at least one table
        assertThat(tableCount).isNotNull();
        assertThat(tableCount).isGreaterThan(0);
    }
}