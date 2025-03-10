package netto.leonidas.avanade_decola.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

@AllArgsConstructor
public class MigrationStrategy {

    private final Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void executeMigration() {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try (FileOutputStream fos = new FileOutputStream("liquibase.log");
             PrintStream logStream = new PrintStream(fos)) {

            System.setOut(logStream);
            System.setErr(logStream);

            try (Connection conn = getConnection();
                 JdbcConnection jdbcConnection = new JdbcConnection(conn)) {

                Liquibase liquibase = new Liquibase(
                        "src/main/resources/db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection
                );
                liquibase.update();

            } catch (SQLException | LiquibaseException e) {
                System.setErr(originalErr);
                originalErr.println("Erro ao executar a migração: " + e.getMessage());
                e.printStackTrace(originalErr);
            }

        } catch (IOException ex) {
            originalErr.println("Erro ao redirecionar logs: " + ex.getMessage());
            ex.printStackTrace(originalErr);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}

