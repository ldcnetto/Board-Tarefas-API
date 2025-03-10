package netto.leonidas.avanade_decola.service;

import netto.leonidas.avanade_decola.persistence.dao.BoardColumnDAO;
import netto.leonidas.avanade_decola.persistence.entity.BoardColumnEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BoardColumnQueryService {

    private final BoardColumnDAO boardColumnDAO;

    public BoardColumnQueryService(BoardColumnDAO boardColumnDAO) {
        this.boardColumnDAO = boardColumnDAO;
    }

    public Optional<BoardColumnEntity> findById(final Long id) {
        return boardColumnDAO.findById(id);
    }
}