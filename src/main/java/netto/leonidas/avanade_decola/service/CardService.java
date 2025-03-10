package netto.leonidas.avanade_decola.service;

import netto.leonidas.avanade_decola.dto.BoardColumnInfoDTO;
import netto.leonidas.avanade_decola.exception.CardBlockedException;
import netto.leonidas.avanade_decola.exception.CardFinishedException;
import netto.leonidas.avanade_decola.exception.EntityNotFoundException;
import netto.leonidas.avanade_decola.persistence.dao.BlockDAO;
import netto.leonidas.avanade_decola.persistence.dao.CardDAO;
import netto.leonidas.avanade_decola.persistence.entity.CardEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static netto.leonidas.avanade_decola.persistence.entity.BoardColumnKindEnum.CANCEL;
import static netto.leonidas.avanade_decola.persistence.entity.BoardColumnKindEnum.FINAL;

@Service
@Transactional
public class CardService {

    private final CardDAO cardDAO;
    private final BlockDAO blockDAO;

    public CardService(CardDAO cardDAO, BlockDAO blockDAO) {
        this.cardDAO = cardDAO;
        this.blockDAO = blockDAO;
    }

    public CardEntity create(final CardEntity entity) {
        cardDAO.insert(entity);
        return entity;
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) {
        var optional = cardDAO.findById(cardId);
        var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
        );
        if (dto.blocked()) {
            var message = "O card %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId);
            throw new CardBlockedException(message);
        }
        var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
        if (currentColumn.kind().equals(FINAL)) {
            throw new CardFinishedException("O card já foi finalizado");
        }
        var nextColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado"));
        cardDAO.moveToColumn(nextColumn.id(), cardId);
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) {
        var optional = cardDAO.findById(cardId);
        var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId))
        );
        if (dto.blocked()) {
            var message = "O card %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId);
            throw new CardBlockedException(message);
        }
        var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
        if (currentColumn.kind().equals(FINAL)) {
            throw new CardFinishedException("O card já foi finalizado");
        }
        boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado"));
        cardDAO.moveToColumn(cancelColumnId, cardId);
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) {
        var optional = cardDAO.findById(id);
        var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
        );
        if (dto.blocked()) {
            var message = "O card %s já está bloqueado".formatted(id);
            throw new CardBlockedException(message);
        }
        var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow();
        if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)) {
            var message = "O card está em uma coluna do tipo %s e não pode ser bloqueado"
                    .formatted(currentColumn.kind());
            throw new IllegalStateException(message);
        }
        blockDAO.block(reason, id);
    }

    public void unblock(final Long id, final String reason) {
        var optional = cardDAO.findById(id);
        var dto = optional.orElseThrow(
                () -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id))
        );
        if (!dto.blocked()) {
            var message = "O card %s não está bloqueado".formatted(id);
            throw new CardBlockedException(message);
        }
        blockDAO.unblock(reason, id);
    }
}