package br.com.jack.pedidos.security;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final JdbcTemplate jdbc;

    public DatabaseUserDetailsService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            DatabaseUser user = jdbc.queryForObject("""
                    SELECT id, username, password_hash, active
                    FROM users
                    WHERE LOWER(username) = LOWER(?)
                    """, (resultSet, rowNumber) -> new DatabaseUser(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    resultSet.getString("password_hash"),
                    resultSet.getBoolean("active")), username.trim());

            List<SimpleGrantedAuthority> authorities = jdbc.query("""
                    SELECT role_table.code
                    FROM roles role_table
                    JOIN user_roles user_role ON user_role.role_id = role_table.id
                    WHERE user_role.user_id = ? AND role_table.active = TRUE
                    """, (resultSet, rowNumber) -> new SimpleGrantedAuthority("ROLE_" + resultSet.getString("code")), user.id());

            return User.withUsername(user.username())
                    .password(user.passwordHash())
                    .disabled(!user.active())
                    .authorities(authorities)
                    .build();
        } catch (EmptyResultDataAccessException exception) {
            throw new UsernameNotFoundException("Usuário não encontrado.");
        }
    }

    private record DatabaseUser(Long id, String username, String passwordHash, boolean active) { }
}
