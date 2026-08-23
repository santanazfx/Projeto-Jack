package br.com.jack.pedidos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:jacktest;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
class DatabaseSchemaIntegrationTest {
    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @Transactional
    void persistsUpdatesAndDeletesOperationalDataWithRelationships() {
        Integer products = jdbc.queryForObject("select count(*) from products", Integer.class);
        Integer roles = jdbc.queryForObject("select count(*) from roles", Integer.class);
        Integer queueTables = jdbc.queryForObject("select count(*) from information_schema.tables where table_name in ('kitchen_queue', 'print_queue', 'payments', 'deliveries', 'audit_logs')", Integer.class);
        assertThat(products).isEqualTo(12);
        assertThat(roles).isEqualTo(6);
        assertThat(queueTables).isEqualTo(5);

        jdbc.update("insert into users (name, username, email, phone, password_hash, active) values (?, ?, ?, ?, ?, ?)", "Operador de Teste", "operador de teste", "operador@teste.local", "11999990000", "$2a$10$hash-de-teste", true);
        Long userId = jdbc.queryForObject("select id from users where email = ?", Long.class, "operador@teste.local");
        Long adminRole = jdbc.queryForObject("select id from roles where code = 'ADMIN'", Long.class);
        jdbc.update("insert into user_roles (user_id, role_id) values (?, ?)", userId, adminRole);
        Integer relationCount = jdbc.queryForObject("select count(*) from user_roles where user_id = ? and role_id = ?", Integer.class, userId, adminRole);
        assertThat(relationCount).isEqualTo(1);

        jdbc.update("update products set available = false where id = 12");
        Boolean unavailable = jdbc.queryForObject("select available from products where id = 12", Boolean.class);
        assertThat(unavailable).isFalse();

        Integer qrCount = jdbc.queryForObject("select count(*) from table_qr_tokens token join restaurant_tables restaurant_table on restaurant_table.id = token.table_id where restaurant_table.number = '01' and token.active = true", Integer.class);
        assertThat(qrCount).isEqualTo(1);

        jdbc.update("delete from user_roles where user_id = ?", userId);
        jdbc.update("delete from users where id = ?", userId);
        Integer deleted = jdbc.queryForObject("select count(*) from users where id = ?", Integer.class, userId);
        assertThat(deleted).isZero();
    }
}
