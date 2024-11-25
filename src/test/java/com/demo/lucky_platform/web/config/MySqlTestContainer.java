package com.demo.lucky_platform.web.config;

import org.junit.jupiter.api.DisplayName;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;


@DisplayName("MySql Test Containers")
@ActiveProfiles("test")
@Configuration
public class MySqlTestContainer {

    private static final String MYSQL_DOCKER_IMAGE = "mysql:8.0";
    private static final int MYSQL_PORT = 3306;
    private static final String MYSQL_DATABASE = "lucky";
    private static final String MYSQL_USER = "user";
    private static final String MYSQL_PASSWORD = "1234";

    public static MySQLContainer<?> mysqlContainer;

    static {
        mysqlContainer = new MySQLContainer<>(DockerImageName.parse(MYSQL_DOCKER_IMAGE))
                .withExposedPorts(MYSQL_PORT)
                .withDatabaseName(MYSQL_DATABASE)
                .withUsername(MYSQL_USER)
                .withPassword(MYSQL_PASSWORD)
                .withReuse(true)
                .withUrlParam("useSSL", "false")
                .withUrlParam("allowPublicKeyRetrieval", "true");

        if (!mysqlContainer.isRunning()) {
            mysqlContainer.start();
        }

        System.out.println("mysql started!!");

        mysqlContainer.start();

        System.setProperty("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
        System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
    }

}
