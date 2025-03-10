package netto.leonidas.avanade_decola.persistence.dao;

import netto.leonidas.avanade_decola.dto.CardDetailsDTO;
import netto.leonidas.avanade_decola.persistence.converter.OffsetDateTimeConverter;
import netto.leonidas.avanade_decola.persistence.entity.CardEntity;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Repository
public class CardDAO {

    private final JdbcTemplate jdbcTemplate;

    public CardDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(final CardEntity entity) {
        var sql = "INSERT INTO CARDS (title, description, board_column_id) VALUES (?, ?, ?);";
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, entity.getTitle());
            ps.setString(2, entity.getDescription());
            ps.setLong(3, entity.getBoardColumn().getId());
            return ps;
        }, keyHolder);

        entity.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    public void moveToColumn(final Long columnId, final Long cardId) {
        var sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?;";
        jdbcTemplate.update(sql, columnId, cardId);
    }

    public Optional<CardDetailsDTO> findById(final Long id) {
        var sql =
                """
                SELECT c.id,
                       c.title,
                       c.description,
                       b.blocked_at,
                       b.block_reason,
                       c.board_column_id,
                       bc.name,
                       (SELECT COUNT(sub_b.id)
                          FROM BLOCKS sub_b
                         WHERE sub_b.card_id = c.id) AS blocks_amount
                  FROM CARDS c
                  LEFT JOIN BLOCKS b
                    ON c.id = b.card_id
                   AND b.unblocked_at IS NULL
                 INNER JOIN BOARDS_COLUMNS bc
                    ON bc.id = c.board_column_id
                 WHERE c.id = ?;
                """;
        var result = jdbcTemplate.query(sql, new ArgumentPreparedStatementSetter(new Object[]{id}), (rs, rowNum) -> {
            var dto = new CardDetailsDTO(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    nonNull(rs.getString("block_reason")),
                    OffsetDateTimeConverter.toOffsetDateTime(rs.getTimestamp("blocked_at")),
                    rs.getString("block_reason"),
                    rs.getInt("blocks_amount"),
                    rs.getLong("board_column_id"),
                    rs.getString("name")
            );
            return dto;
        });
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}