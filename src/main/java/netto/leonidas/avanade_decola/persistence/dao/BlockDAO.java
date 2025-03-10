package netto.leonidas.avanade_decola.persistence.dao;

import netto.leonidas.avanade_decola.persistence.converter.OffsetDateTimeConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public class BlockDAO {

    private final JdbcTemplate jdbcTemplate;

    public BlockDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void block(final String reason, final Long cardId) {
        var sql = "INSERT INTO BLOCKS (blocked_at, block_reason, card_id) VALUES (?, ?, ?);";
        jdbcTemplate.update(sql, OffsetDateTimeConverter.toTimestamp(OffsetDateTime.now()), reason, cardId);
    }

    public void unblock(final String reason, final Long cardId) {
        var sql = "UPDATE BLOCKS SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL;";
        jdbcTemplate.update(sql, OffsetDateTimeConverter.toTimestamp(OffsetDateTime.now()), reason, cardId);
    }
}