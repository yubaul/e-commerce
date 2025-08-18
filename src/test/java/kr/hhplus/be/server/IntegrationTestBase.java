package kr.hhplus.be.server;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestBase {

    private static final MySQLContainer<?> MYSQL_CONTAINER;
    private static final GenericContainer<?> REDIS_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
                .withDatabaseName("hhplus")
                .withUsername("application")
                .withPassword("application");
        MYSQL_CONTAINER.start();

        // Redis (단일)
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
                .withExposedPorts(6379)
                .waitingFor(Wait.forListeningPort());
        REDIS_CONTAINER.start();

        System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
    }

    @BeforeAll
    void initSchemaAndDataOnce() throws Exception {
        try (
                Connection conn = DriverManager.getConnection(
                        MYSQL_CONTAINER.getJdbcUrl(),
                        MYSQL_CONTAINER.getUsername(),
                        MYSQL_CONTAINER.getPassword());
                Statement stmt = conn.createStatement()
        ) {
            String schema = new String(new ClassPathResource("sql/schema.sql").getInputStream().readAllBytes());
            String data = new String(new ClassPathResource("sql/data.sql").getInputStream().readAllBytes());

            for (String sql : schema.split(";")) {
                if (!sql.isBlank()) stmt.execute(sql.trim());
            }
            for (String sql : data.split(";")) {
                if (!sql.isBlank()) stmt.execute(sql.trim());
            }
        }
    }

}