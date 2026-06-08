package ru.butenko;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LiquibaseMigrationIT extends BaseIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateSchemaAndSeedData() {
        Integer usersTableCount = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_schema = 'public' and table_name = 'users'",
                Integer.class
        );
        Integer carsTableCount = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_schema = 'public' and table_name = 'cars'",
                Integer.class
        );
        Integer userCount = jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
        String managerName = jdbcTemplate.queryForObject(
                "select name from users where id = '11111111-1111-1111-1111-111111111111'",
                String.class
        );
        assertEquals(1, usersTableCount);
        assertEquals(1, carsTableCount);
        assertTrue(userCount >= 2);
        assertEquals("Manager #1", managerName);
    }
}
