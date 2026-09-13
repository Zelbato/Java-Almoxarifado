
package com.mycompany.almoxarifado.config;

import org.flywaydb.core.Flyway;

/**
 *
 * @author heito
 */
public class MigrationConfig {
    
    private static final String URL =
            "jdbc:mysql://localhost:3306/almoxarifado_db";
    
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(URL, USER, PASSWORD)
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
    }
}
