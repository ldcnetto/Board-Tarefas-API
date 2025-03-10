package netto.leonidas.avanade_decola.ui;

import lombok.AllArgsConstructor;
import netto.leonidas.avanade_decola.dto.BoardColumnInfoDTO;
import netto.leonidas.avanade_decola.persistence.entity.BoardColumnEntity;
import netto.leonidas.avanade_decola.persistence.entity.BoardEntity;
import netto.leonidas.avanade_decola.persistence.entity.CardEntity;
import netto.leonidas.avanade_decola.service.BoardColumnQueryService;
import netto.leonidas.avanade_decola.service.BoardQueryService;
import netto.leonidas.avanade_decola.service.CardQueryService;
import netto.leonidas.avanade_decola.service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@AllArgsConstructor
public class BoardMenu {

    private static final Logger logger = LoggerFactory.getLogger(BoardMenu.class);

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private final CardService cardService;
    private final BoardQueryService boardQueryService;
    private final BoardColumnQueryService boardColumnQueryService;
    private final CardQueryService cardQueryService;

    public void execute(BoardEntity entity) {
        try {
            System.out.printf("Bem vindo ao board %s, selecione a operação desejada\n", entity.getId());
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Ver board");
                System.out.println("7 - Ver coluna com cards");
                System.out.println("8 - Ver card");
                System.out.println("9 - Voltar para o menu anterior");
                System.out.println("10 - Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard(entity);
                    case 2 -> moveCardToNextColumn(entity);
                    case 3 -> blockCard(entity);
                    case 4 -> unblockCard(entity);
                    case 5 -> cancelCard(entity);
                    case 6 -> showBoard(entity);
                    case 7 -> showColumn(entity);
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando para o menu anterior");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida, informe uma opção do menu");
                }
            }
        } catch (Exception ex) {
            logger.error("Erro durante a execução do menu", ex);
            System.exit(1);
        }
    }

    private void createCard(BoardEntity entity) {
        var card = new CardEntity();
        System.out.println("Informe o título do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        cardService.create(card);
    }

    private void moveCardToNextColumn(BoardEntity entity) {
        System.out.println("Informe o id do card que deseja mover para a próxima coluna");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try {
            cardService.moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex) {
            logger.error("Erro ao mover o card", ex);
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard(BoardEntity entity) {
        System.out.println("Informe o id do card que será bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try {
            cardService.block(cardId, reason, boardColumnsInfo);
        } catch (RuntimeException ex) {
            logger.error("Erro ao bloquear o card", ex);
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard(BoardEntity entity) {
        System.out.println("Informe o id do card que será desbloqueado");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do desbloqueio do card");
        var reason = scanner.next();
        try {
            cardService.unblock(cardId, reason);
        } catch (RuntimeException ex) {
            logger.error("Erro ao desbloquear o card", ex);
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard(BoardEntity entity) {
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try {
            cardService.cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex) {
            logger.error("Erro ao cancelar o card", ex);
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard(BoardEntity entity) {
        var optional = boardQueryService.showBoardDetails(entity.getId());
        optional.ifPresent(b -> {
            System.out.printf("Board [%s,%s]\n", b.id(), b.name());
            b.columns().forEach(c ->
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount())
            );
        });
    }

    private void showColumn(BoardEntity entity) {
        var columnsIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        while (!columnsIds.contains(selectedColumnId)) {
            System.out.printf("Escolha uma coluna do board %s pelo id\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong();
        }
        var column = boardColumnQueryService.findById(selectedColumnId);
        column.ifPresent(co -> {
            System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
            co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s",
                    ca.getId(), ca.getTitle(), ca.getDescription()));
        });
    }

    private void showCard() {
        System.out.println("Informe o id do card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        cardQueryService.findById(selectedCardId)
                .ifPresentOrElse(
                        c -> {
                            System.out.printf("Card %s - %s.\n", c.id(), c.title());
                            System.out.printf("Descrição: %s\n", c.description());
                            System.out.println(c.blocked() ?
                                    "Está bloqueado. Motivo: " + c.blockReason() :
                                    "Não está bloqueado");
                            System.out.printf("Já foi bloqueado %s vezes\n", c.blocksAmount());
                            System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                        },
                        () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));
    }
}