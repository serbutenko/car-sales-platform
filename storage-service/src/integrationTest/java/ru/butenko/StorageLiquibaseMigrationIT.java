package ru.butenko;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class StorageLiquibaseMigrationIT extends BaseIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateStorageSchemaAndSeedInventory() {
        assertTableExists("assembly_orders");
        assertTableExists("storage_cars");
        assertTableExists("storage_components");
        assertTableExists("outbox_events");
        assertTableExists("processed_messages");
        assertColumnExists("outbox_events", "attempts");
        assertColumnExists("outbox_events", "last_error");
        assertColumnExists("processed_messages", "consumer_name");

        Integer carsCount = jdbcTemplate.queryForObject("select count(*) from storage_cars", Integer.class);
        Integer componentsCount = jdbcTemplate.queryForObject("select count(*) from storage_components", Integer.class);
        Integer availableCarsCount = jdbcTemplate.queryForObject(
                "select count(*) from storage_cars where status = 'AVAILABLE'",
                Integer.class
        );

        assertEquals(1, carsCount);
        assertTrue(componentsCount >= 4);
        assertEquals(1, availableCarsCount);
    }

    private void assertTableExists(String tableName) {
        Integer tableCount = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.tables where table_schema = 'public' and table_name = ?",
                Integer.class,
                tableName
        );

        assertEquals(1, tableCount);
    }

    private void assertColumnExists(String tableName, String columnName) {
        Integer columnCount = jdbcTemplate.queryForObject(
                """
                        select count(*)
                        from information_schema.columns
                        where table_schema = 'public'
                          and table_name = ?
                          and column_name = ?
                        """,
                Integer.class,
                tableName,
                columnName
        );

        assertEquals(1, columnCount);
    }
}
