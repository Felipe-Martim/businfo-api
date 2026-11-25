package br.com.businfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class BusInfoApplication {

    private static final Logger log = LoggerFactory.getLogger(BusInfoApplication.class);

    public static void main(String[] args) throws IOException {
        // O SQLite cria o arquivo do banco, mas não a pasta.
        Files.createDirectories(Path.of("database"));
        SpringApplication.run(BusInfoApplication.class, args);
    }

    @EventListener
    public void exibirEnderecos(ApplicationReadyEvent evento) {
        Environment env = evento.getApplicationContext().getEnvironment();
        String base = "http://localhost:" + env.getProperty("local.server.port", "3000");
        log.info("BusInfo API v1.1 pronta");
        log.info("  Demonstração funcional: {}", base);
        log.info("  Documentação Swagger:   {}/api-docs", base);
        log.info("  Endpoint de linhas:     {}/api/v1/linhas", base);
        log.info("  Banco de dados SQLite:  {}", env.getProperty("businfo.database.path"));
    }
}
