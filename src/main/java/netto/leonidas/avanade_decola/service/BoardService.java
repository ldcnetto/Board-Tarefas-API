package netto.leonidas.avanade_decola.service;

import netto.leonidas.avanade_decola.persistence.dao.BoardColumnDAO;
import netto.leonidas.avanade_decola.persistence.dao.BoardDAO;
import netto.leonidas.avanade_decola.persistence.entity.BoardEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BoardService {

    private final BoardDAO boardDAO;
    private final BoardColumnDAO boardColumnDAO;

    public BoardService(BoardDAO boardDAO, BoardColumnDAO boardColumnDAO) {
        this.boardDAO = boardDAO;
        this.boardColumnDAO = boardColumnDAO;
    }

    public BoardEntity insert(final BoardEntity entity) {
        boardDAO.insert(entity);
        var columns = entity.getBoardColumns().stream().map(c -> {
            c.setBoard(entity);
            return c;
        }).toList();
        for (var column : columns) {
            boardColumnDAO.insert(column);
        }
        return entity;
    }

    public boolean delete(final Long id) {
        if (!boardDAO.exists(id)) {
            return false;
        }
        boardDAO.delete(id);
        return true;
    }
}