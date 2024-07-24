package br.com.joao.backend.job;

import br.com.joao.backend.transaction.Transaction;
import br.com.joao.backend.transaction.api.TransactionApi;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {
    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository;

    public BatchConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
    }

    @Bean
    Job job(Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step step(
            ItemReader<TransactionApi> reader,
            ItemProcessor<TransactionApi, Transaction> processor,
            ItemWriter<Transaction> writer
    ) {
        return new StepBuilder("step", jobRepository)
                .<TransactionApi, Transaction>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @StepScope
    @Bean
    FlatFileItemReader<TransactionApi> reader(
           @Value("#{jobParameters['cnabFile']}") Resource resource) {
        return new FlatFileItemReaderBuilder<TransactionApi>()
                .name("reader")
                .resource(resource)
                .fixedLength()
                .columns(new Range(1, 1), new Range(2, 9), new Range(10, 19), new Range(20, 30),
                        new Range(31, 42), new Range(43, 48), new Range(49, 62), new Range(63, 80))
                .names("type", "date", "value", "cpf", "card", "hour", "owner", "name")
                .targetType(TransactionApi.class)
                .build();
    }

    @Bean
    ItemProcessor<TransactionApi, Transaction> processor() {
        return item -> {
          var trans = new Transaction(null, item.type(), null, null, item.cpf(),
                  item.card(), null, item.owner().trim(), item.name().trim());

          trans.setValue(trans.withValue(item.value()));
          trans.setDate(trans.withDate(item.date()));
          trans.setHour(trans.withHour(item.hour()));

          return trans;
        };
    }

    @Bean
    JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .sql("""
                    INSERT INTO transaction
                    ("type", "date", "value", cpf, card, "hour", owner, "name")
                    VALUES (:type, :date, :value, :cpf, :card, :hour, :owner, :name)
                 """)
                .beanMapped()
                .build();
    }

    @Bean
    JobLauncher jobLauncherAsync(JobRepository jobRepository) throws Exception{
        var jobLoucher = new TaskExecutorJobLauncher();
        jobLoucher.setJobRepository(jobRepository);
        // TODO: Por padrão é executado com sync. Customizando, usando o @Bean, transformamos para async.
        jobLoucher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLoucher.afterPropertiesSet();

        return jobLoucher;
    }
}
