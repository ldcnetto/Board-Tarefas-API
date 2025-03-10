package netto.leonidas.avanade_decola.service;

import netto.leonidas.avanade_decola.dto.BoardDetailsDTO;
import netto.leonidas.avanade_decola.persistence.dao.BoardColumnDAO;
import netto.leonidas.avanade_decola.persistence.dao.BoardDAO;
import netto.leonidas.avanade_decola.persistence.entity.BoardEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BoardQueryService {

    private final BoardDAO boardDAO;
    private final BoardColumnDAO boardColumnDAO;

    public BoardQueryService(BoardDAO boardDAO, BoardColumnDAO boardColumnDAO) {
        this.boardDAO = boardDAO;
        this.boardColumnDAO = boardColumnDAO;
    }

    public Optional<BoardEntity> findById(final Long id) {
        var optional = boardDAO.findById(id);
        if (optional.isPresent()) {
            var entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    public Optional<BoardDetailsDTO> showBoardDetails(final Long id) {
        var optional = boardDAO.findById(id);
        if (optional.isPresent()) {
            var entity = optional.get();
            var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            var dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
            return Optional.of(dto);
        }
        return Optional.empty();
    }
}