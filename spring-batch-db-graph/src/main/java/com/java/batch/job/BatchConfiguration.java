package com.java.batch.job;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.java.batch.dto.PeopleDTO;
import com.java.batch.mapper.PeopleDBRowMapper;
import com.java.batch.model.People;
import com.java.batch.processor.PeopleProcessor;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

//@Configuration
public class BatchConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	
	@Autowired
	private DataSource dataSource;

	@Autowired
	private PeopleProcessor peopleProcessor;

	@Bean
	@Qualifier(value = "peopleData")
	public Job asyncJob() throws Exception {
		return this.jobBuilderFactory.get("Asynchronous Processing JOB")
				.incrementer(new RunIdIncrementer())
				.start(asyncManagerStep()).build();
	}

	/*
	 * @Qualifier(value = "peopleData")
	 * 
	 * @Bean public Job peopleJob() throws Exception { return
	 * this.jobBuilderFactory.get("peopleData").start(step1Demo3()).build(); }
	 */
	
	/*
	 * @Bean public Step step1Demo3() throws Exception { return
	 * this.stepBuilderFactory.get("step1").<People,
	 * PeopleDTO>chunk(1000).reader(employeeDBReader())
	 * .processor(peopleProcessor).writer(writeToRest()).build(); }
	 */
	@Bean
	public Step asyncManagerStep() throws Exception {
		return stepBuilderFactory.get("Asynchronous Processing : Read -> Process -> Write ")
				.<People, Future<PeopleDTO>>chunk(1000).reader(employeeDBReader()).processor(asyncProcessor())
				.writer(asyncWriter()).taskExecutor(taskExecutor()).build();
	}

	@Bean
	public ItemStreamReader<People> employeeDBReader() {
		JdbcCursorItemReader<People> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("select * from people where age > 190 AND age < 200");
		reader.setRowMapper(new PeopleDBRowMapper());
		reader.setVerifyCursorPosition(false);
		return reader;
	}

	@Bean
	public AsyncItemProcessor<People, PeopleDTO> asyncProcessor() {
		AsyncItemProcessor<People, PeopleDTO> asyncItemProcessor = new AsyncItemProcessor<>();
		asyncItemProcessor.setDelegate(peopleProcessor);
		asyncItemProcessor.setTaskExecutor(taskExecutor());

		return asyncItemProcessor;
	}

	@Bean
	public AsyncItemWriter<PeopleDTO> asyncWriter() throws Exception {
		AsyncItemWriter<PeopleDTO> asyncItemWriter = new AsyncItemWriter<>();
		asyncItemWriter.setDelegate(writeToRest());
		return asyncItemWriter;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(64);
		executor.setMaxPoolSize(64);
		executor.setQueueCapacity(64);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setThreadNamePrefix("MultiThreaded-");
		return executor;
	}

//////////////////////////////////////////
	@Value("${centeral.update.endpoint}")
	private String centralUpdateEndpoint;

	@Bean
//@Async
	public ItemWriter<PeopleDTO> writeToRest() throws Exception {

		return items -> {
			JSONArray jsonArr = new JSONArray();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			for (PeopleDTO people : items) {
				jsonArr.add(this.createJsonObject(people));
			}

			HttpEntity entity2 = new HttpEntity(jsonArr, headers);

			new RestTemplate().exchange(this.centralUpdateEndpoint, HttpMethod.POST, entity2, Void.class);

		};
	}

	private JSONObject createJsonObject(PeopleDTO peopleDTO) {
		JSONObject jsonObj2 = new JSONObject();
		jsonObj2.put("id", String.valueOf(peopleDTO.getPeopleId()));
		jsonObj2.put("peopleId", peopleDTO.getPeopleId());
		jsonObj2.put("firstName", peopleDTO.getFirstName());
		jsonObj2.put("lastName", peopleDTO.getLastName());
		jsonObj2.put("age", peopleDTO.getAge());
		return jsonObj2;
	}

	private Resource outputResource = new FileSystemResource("output/people_output.csv");
	@Bean
	public ItemWriter<PeopleDTO> employeeFileWriter() throws Exception {
		FlatFileItemWriter<PeopleDTO> writer = new FlatFileItemWriter<>();
		writer.setResource(outputResource);
		writer.setLineAggregator(new DelimitedLineAggregator<PeopleDTO>() {
			{
				setFieldExtractor(new BeanWrapperFieldExtractor<PeopleDTO>() {
					{
						setNames(new String[] { "peopleId", "firstName", "lastName", "email", "age" });
					}
				});
			}
		});
		writer.setShouldDeleteIfExists(true);
		return writer;
	}


}
