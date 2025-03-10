package netto.leonidas.avanade_decola.dto;

import netto.leonidas.avanade_decola.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id,
                             String name,
                             BoardColumnKindEnum kind,
                             int cardsAmount) {
}