package netto.leonidas.avanade_decola.service;

import netto.leonidas.avanade_decola.dto.CardDetailsDTO;
import netto.leonidas.avanade_decola.persistence.dao.CardDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CardQueryService {

    private final CardDAO cardDAO;

    public CardQueryService(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    public Optional<CardDetailsDTO> findById(final Long id) {
        return cardDAO.findById(id);
    }
}