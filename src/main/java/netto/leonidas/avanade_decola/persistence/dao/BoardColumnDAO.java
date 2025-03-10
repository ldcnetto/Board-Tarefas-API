package netto.leonidas.avanade_decola.persistence.dao;

import netto.leonidas.avanade_decola.dto.BoardColumnDTO;
import netto.leonidas.avanade_decola.persistence.entity.BoardColumnEntity;
import netto.leonidas.avanade_decola.persistence.entity.CardEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static netto.leonidas.avanade_decola.persistence.entity.BoardColumnKindEnum.findByName;

@Repository
public class BoardColumnDAO {

    private final JdbcTemplate jdbcTemplate;

    public BoardColumnDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final BoardColumnEntity entity) {
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?);";
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getOrder());
            ps.setString(3, entity.getKind().name());
            ps.setLong(4, entity.getBoard().getId());
            return ps;
        }, keyHolder);

        entity.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    public List<BoardColumnEntity> findByBoardId(final Long boardId) {
        var sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`";
        return jdbcTemplate.query(sql, new ArgumentPreparedStatementSetter(new Object[]{boardId}), new BoardColumnRowMapper());
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) {
        var sql =
                """
                SELECT bc.id,
                       bc.name,
                       bc.kind,
                       (SELECT COUNT(c.id)
                               FROM CARDS c
                              WHERE c.board_column_id = bc.id) cards_amount
                  FROM BOARDS_COLUMNS bc
                 WHERE board_id = ?
                 ORDER BY `order`;
                """;
        return jdbcTemplate.query(sql, new ArgumentPreparedStatementSetter(new Object[]{boardId}), new BoardColumnDTORowMapper());
    }

    public Optional<BoardColumnEntity> findById(final Long boardId) {
        var sql =
                """
                SELECT bc.name,
                       bc.kind,
                       c.id AS card_id,
                       c.title AS card_title,
                       c.description AS card_description
                  FROM BOARDS_COLUMNS bc
                  LEFT JOIN CARDS c
                    ON c.board_column_id = bc.id
                 WHERE bc.id = ?;
                """;
        var result = jdbcTemplate.query(sql, new ArgumentPreparedStatementSetter(new Object[]{boardId}), new BoardColumnWithCardsRowMapper());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    private static class BoardColumnRowMapper implements RowMapper<BoardColumnEntity> {
        @Override
        public BoardColumnEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            var entity = new BoardColumnEntity();
            entity.setId(rs.getLong("id"));
            entity.setName(rs.getString("name"));
            entity.setOrder(rs.getInt("order"));
            entity.setKind(findByName(rs.getString("kind")));
            return entity;
        }
    }

    private static class BoardColumnDTORowMapper implements RowMapper<BoardColumnDTO> {
        @Override
        public BoardColumnDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BoardColumnDTO(
                    rs.getLong("id"),
                    rs.getString("name"),
                    findByName(rs.getString("kind")),
                    rs.getInt("cards_amount")
            );
        }
    }

    private static class BoardColumnWithCardsRowMapper implements RowMapper<BoardColumnEntity> {
        @Override
        public BoardColumnEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            var entity = new BoardColumnEntity();
            entity.setName(rs.getString("name"));
            entity.setKind(findByName(rs.getString("kind")));

            do {
                if (rs.getString("card_title") == null) {
                    break;
                }
                var card = new CardEntity();
                card.setId(rs.getLong("card_id"));
                card.setTitle(rs.getString("card_title"));
                card.setDescription(rs.getString("card_description"));
                entity.getCards().add(card);
            } while (rs.next());

            return entity;
        }
    }
}