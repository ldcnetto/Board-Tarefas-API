package netto.leonidas.avanade_decola.ui;

import netto.leonidas.avanade_decola.persistence.entity.BoardColumnEntity;
import netto.leonidas.avanade_decola.persistence.entity.BoardColumnKindEnum;
import netto.leonidas.avanade_decola.persistence.entity.BoardEntity;
import netto.leonidas.avanade_decola.service.BoardQueryService;
import netto.leonidas.avanade_decola.service.BoardService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static netto.leonidas.avanade_decola.persistence.entity.BoardColumnKindEnum.*;

@Component
public class MainMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardService boardService;
    private final BoardQueryService boardQueryService;
    private final BoardMenu boardMenu;

    public MainMenu(BoardService boardService, BoardQueryService boardQueryService, BoardMenu boardMenu) {
        this.boardService = boardService;
        this.boardQueryService = boardQueryService;
        this.boardMenu = boardMenu;
    }

    public void execute() {
        System.out.println("Bem vindo ao gerenciador de boards, escolha a opção desejada");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            switch (option) {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida, informe uma opção do menu");
            }
        }
    }

    private void createBoard() {
        var entity = new BoardEntity();
        System.out.println("Informe o nome do seu board");
        entity.setName(scanner.next());

        System.out.println("Seu board terá colunas além das 3 padrões? Se sim informe quantas, senão digite '0'");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial do board");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna de tarefa pendente do board");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento do board");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        boardService.insert(entity);
        System.out.println("Board criado com sucesso!");
    }

    private void selectBoard() {
        System.out.println("Informe o id do board que deseja selecionar");
        var id = scanner.nextLong();
        var optional = boardQueryService.findById(id);
        optional.ifPresentOrElse(
                boardMenu::execute, // Passa o BoardEntity para o BoardMenu
                () -> System.out.printf("Não foi encontrado um board com id %s\n", id)
        );
    }

    private void deleteBoard() {
        System.out.println("Informe o id do board que será excluído");
        var id = scanner.nextLong();
        if (boardService.delete(id)) {
            System.out.printf("O board %s foi excluído\n", id);
        } else {
            System.out.printf("Não foi encontrado um board com id %s\n", id);
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}