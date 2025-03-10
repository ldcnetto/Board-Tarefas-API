package netto.leonidas.avanade_decola.persistence.dao;

import netto.leonidas.avanade_decola.persistence.entity.BoardEntity;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class BoardDAO {

    private final JdbcTemplate jdbcTemplate;

    public BoardDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final BoardEntity entity) {
        var sql = "INSERT INTO BOARDS (name) VALUES (?);";
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, entity.getName());
            return ps;
        }, keyHolder);

        entity.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    public void delete(final Long id) {
        var sql = "DELETE FROM BOARDS WHERE id = ?;";
        jdbcTemplate.update(sql, id);
    }

    public Optional<BoardEntity> findById(final Long id) {
        var sql = "SELECT id, name FROM BOARDS WHERE id = ?;";
        var result = jdbcTemplate.query(sql, new ArgumentPreparedStatementSetter(new Object[]{id}), (rs, rowNum) -> {
            var entity = new BoardEntity();
            entity.setId(rs.getLong("id"));
            entity.setName(rs.getString("name"));
            return entity;
        });
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public boolean exists(final Long id) {
        var sql = "SELECT 1 FROM BOARDS WHERE id = ?;";
        var result = jdbcTemplate.queryForList(sql, id);
        return !result.isEmpty();
    }
}