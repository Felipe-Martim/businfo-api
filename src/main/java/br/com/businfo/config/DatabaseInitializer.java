package br.com.businfo.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

/**
 * Cria as tabelas e a carga inicial somente quando o banco ainda não existe,
 * para que os cadastros feitos pela API sobrevivam a um reinício.
 * Roda durante a inicialização do contexto, antes de o servidor aceitar requisições.
 */
@Component
public class DatabaseInitializer implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DataSource dataSource;
    private final boolean reset;

    public DatabaseInitializer(DataSource dataSource,
                               @Value("${businfo.database.reset:false}") boolean reset) {
        this.dataSource = dataSource;
        this.reset = reset;
    }

    @Override
    public void afterPropertiesSet() {
        Integer tabelaLinhas = new JdbcTemplate(dataSource).queryForObject(
                "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = 'linhas'",
                Integer.class);

        if (!reset && tabelaLinhas != null && tabelaLinhas > 0) {
            return;
        }

        ResourceDatabasePopulator scripts = new ResourceDatabasePopulator(
                new ClassPathResource("db/schema.sql"),
                new ClassPathResource("db/seed.sql"));
        scripts.setSqlScriptEncoding("UTF-8");
        scripts.execute(dataSource);
        log.info("Banco de dados criado com a carga inicial (schema.sql + seed.sql)");
    }
}
