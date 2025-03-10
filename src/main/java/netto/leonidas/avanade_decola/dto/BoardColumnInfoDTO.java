package netto.leonidas.avanade_decola.dto;

import netto.leonidas.avanade_decola.persistence.entity.BoardColumnKindEnum;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnKindEnum kind) {
}
