package com.demo.lucky_platform.web.controller;

import com.demo.lucky_platform.web.config.KafkaTestConfig;
import com.demo.lucky_platform.web.config.MySqlTestContainer;
import com.demo.lucky_platform.web.config.RedisTestContainer;
import com.demo.lucky_platform.web.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for controller integration tests.
 * This class sets up the common test environment, including the TestContainers for MySQL and Redis.
 * All controller integration tests should extend this class.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({MySqlTestContainer.class, RedisTestContainer.class, KafkaTestConfig.class, TestSecurityConfig.class})
public abstract class BaseControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
