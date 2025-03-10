package netto.leonidas.avanade_decola.runner;

import netto.leonidas.avanade_decola.ui.MainMenu;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BoardMenuRunner implements CommandLineRunner {

    private final MainMenu mainMenu;

    public BoardMenuRunner(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    @Override
    public void run(String... args) throws Exception {
        mainMenu.execute(); // Inicia o MainMenu
    }
}