package com.demo.lucky_platform.web.batch;

import com.demo.lucky_platform.web.user.domain.User;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class BatchSampleClass {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    public BatchSampleClass(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Defines the main job with conditional flow based on step execution status
     *
     * @return configured job
     */
    @Bean
    public Job exampleJob() {
        // Create a default value for the requestDate parameter
        String defaultRequestDate = "19700101";

        return new JobBuilder("exampleJob-1", jobRepository)
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                // Start with the basic step
                .start(step(defaultRequestDate))
                // If the step fails, go to the failure handling step
                .on(ExitStatus.FAILED.getExitCode())
                .to(failStep())
                // If the basic step completes successfully
                .from(step(defaultRequestDate))
                .on(ExitStatus.COMPLETED.getExitCode())
                .to(successStep()) // Loop back to the same step
                // For any other exit status
                .from(step(defaultRequestDate))
                .on("*")
                .to(absoluteStep())
                .end()
                .build();
    }

    /**
     * Defines the basic step that uses a common tasklet
     *
     * @param requestDate job parameter for the request date (format: yyyyMMdd), defaults to "19700101" if not provided
     * @return configured step
     */
    @Bean
    @JobScope
    public Step step(@Value("#{jobParameters[requestDate] ?: '19700101'}") String requestDate) {
        log.info("Step executed with requestDate: {}", requestDate);
        return new StepBuilder("basicStep", jobRepository)
                .tasklet(createCommonTasklet(requestDate), transactionManager)
                .build();
    }

    /**
     * Defines a step that handles failure scenarios
     *
     * @return configured failure handling step
     */
    @Bean
    @JobScope
    public Step failStep() {
        log.info("FailStep executed");
        return new StepBuilder("failStep", jobRepository)
                .tasklet(createCommonTasklet(), transactionManager)
                .build();
    }

    /**
     * Defines a fallback step for any non-standard exit status
     *
     * @return configured fallback step
     */
    @Bean
    @JobScope
    public Step absoluteStep() {
        log.info("AbsoluteStep executed");
        return new StepBuilder("absoluteStep", jobRepository)
                .tasklet(createCommonTasklet(), transactionManager)
                .build();
    }

    /**
     * Defines a success step that is executed when the basic step completes successfully
     *
     * @return configured success step
     */
    @Bean
    @JobScope
    public Step successStep() {
        log.info("SuccessStep executed");
        return new StepBuilder("successStep", jobRepository)
                .tasklet(createCommonTasklet(), transactionManager)
                .build();
    }

    /**
     * Creates a common tasklet for all steps
     * This simple tasklet logs a message and completes successfully
     *
     * @param requestDate optional job parameter for the request date
     * @return tasklet implementation
     */
    private Tasklet createCommonTasklet(String requestDate) {
        log.info("CommonTasklet executed with requestDate: {}", requestDate);
        return (contribution, chunkContext) -> {
            log.info("Step execution started with requestDate: {}", requestDate);
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * Creates a common tasklet for all steps without parameters
     * This is used by steps that don't need job parameters
     *
     * @return tasklet implementation
     */
    private Tasklet createCommonTasklet() {
        log.info("CommonTasklet executed without parameters");
        return (contribution, chunkContext) -> {
            log.info("Step execution started without parameters");
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * Defines a chunk-oriented processing step that reads, processes, and writes User entities
     *
     * @return configured chunk-oriented step
     */
    @Bean
    @JobScope
    public Step chunkStep() {
        log.info("ChunkStep executed");
        return new StepBuilder("chunkStep", jobRepository)
                .<User, User>chunk(5, transactionManager) // Process in chunks of 5 items
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    /**
     * Creates a JPA item reader that fetches User entities
     *
     * @return configured JPA item reader
     */
    @Bean
    @StepScope
    public JpaPagingItemReader<User> reader() {
        Map<String,Object> parameterValues = new HashMap<>();
        log.info("ItemReader executed");
        return new JpaPagingItemReaderBuilder<User>()
                .pageSize(5) // Process 5 items at a time
                .parameterValues(parameterValues)
                .queryString("SELECT u FROM User u ORDER BY u.id ASC")
                .entityManagerFactory(entityManagerFactory)
                .name("JpaPagingItemReader")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<User, User> processor() {
        log.info("ItemProcessor executed");
        return user -> {
            log.info("Processing user - ID: {} / Name: {} / state: {}",
                    user.getId(), user.getName(), user.getEnabled());
            user.disabled();
            return user;
        };
    }

    /**
     * Creates a JPA item writer that persists the processed User entities
     *
     * @return configured JPA item writer
     */
    @Bean
    @StepScope
    public JpaItemWriter<User> writer() {
        log.info("ItemWriter executed");
        return new JpaItemWriterBuilder<User>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}
