package com.java.batch.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
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
import com.java.batch.kafka.PeopleKafkaSender;
import com.java.batch.listner.ReadStepListner;
import com.java.batch.listner.WriteStepListner;
import com.java.batch.mapper.PeopleDBRowMapper;
import com.java.batch.model.People;
import com.java.batch.processor.PeopleProcessor;
import com.java.batch.reader.PeopleItemStreamReader;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Configuration
public class JobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	//@Autowired
	//private JobSkipPolicy skipPolicy;
	
	@Autowired
	private PeopleItemStreamReader peopleReader;
	
	@Autowired
	private PeopleProcessor peopleProcessor;
	
	private Resource outputResource = new FileSystemResource("output/people_output.csv");

	@Qualifier(value = "peopleData")
	@Bean
	public Job peopleJob() throws Exception {
		return this.jobBuilderFactory.get("peopleData").start(steps()).build();
	}

	@Bean
	public Step steps() throws Exception {
		return this.stepBuilderFactory.get("step1").<People, PeopleDTO>chunk(2)
				//.reader(peopleReader.employeeDBReaderJPA())
				.reader(employeeDBReader())
				.processor(peopleProcessor).writer(this.sendToKafka()).listener(writeStepListner())
				.listener(readStepListner()).build();
		
		//return this.stepBuilderFactory.get("step1").<People, Future<PeopleDTO>>chunk(20000)
			//	.reader(employeeDBReader())
				//.processor(asyncProcessor()).writer(asyncWriter()).listener(writeStepListner())
				//.listener(readStepListner()).build();
	}
	
	@Autowired
	PeopleKafkaSender sender;
	private ItemWriter<? super PeopleDTO> sendToKafka() {
		return items -> {
			sender.write(items);
		};
	}
	
	@Bean
	public PeopleItemStreamReader getReader() {
		return new PeopleItemStreamReader();
	}
	
	/*
	 * @Bean public RepositoryItemReader repoReader() { Map<String, Sort.Direction>
	 * sorts = new HashMap<String, Sort.Direction>(); sorts.put("id",
	 * Direction.ASC);
	 * 
	 * RepositoryItemReader<People> reader = new RepositoryItemReader<>();
	 * reader.setRepository(this.peopleRepo); reader.setMethodName("findAll");
	 * reader.setPageSize(1); reader.setSort(sorts); return reader; }
	 */
	
	/*
	 * @Bean public ItemStreamReader<People> employeeDBReaderJPA(PeopleRepo repo) {
	 * RepositoryItemReader<People> reader = new RepositoryItemReader<>();
	 * reader.setRepository(repo); reader.setMethodName("findAll");
	 * reader.setPageSize(10000); Map<String, Sort.Direction> sorts = new
	 * HashMap<String, Sort.Direction>(); sorts.put("people_id", Direction.ASC);
	 * reader.setSort(sorts); return reader; }
	 */
	
	
	
	@Bean
	public ItemStreamReader<People> employeeDBReader() {
		JdbcCursorItemReader<People> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("select * from people where age > 190 AND age < 200");
		reader.setRowMapper(new PeopleDBRowMapper());
		return reader;
	}
	
		
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
		jsonObj2.put("id", peopleDTO.getId());
		jsonObj2.put("peopleId", peopleDTO.getPeopleId());
		jsonObj2.put("firstName", peopleDTO.getFirstName());
		jsonObj2.put("lastName", peopleDTO.getLastName());
		jsonObj2.put("age", peopleDTO.getAge());
		return jsonObj2;
	}


	
	//////////////////////////////////////////
	@Value("${centeral.update.endpoint}")
	private String centralUpdateEndpoint;

		
	class PeopleTransferDTO {
		List<PeopleDTO> peoplelist;
	}
	
	private void updatePeopleData(List<PeopleDTO> peopleList) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		JSONArray jsonArr = new JSONArray();
		peopleList.forEach(item -> {
			jsonArr.add(createJsonObject(item));
		});

		HttpEntity entity2 = new HttpEntity(jsonArr, headers);

		new RestTemplate().exchange(this.centralUpdateEndpoint, HttpMethod.POST, entity2, Void.class);

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
		System.out.println("-------------------1---------------");
		AsyncItemWriter<PeopleDTO> asyncItemWriter = new AsyncItemWriter<>();
		asyncItemWriter.setDelegate(writeToRest());
		return asyncItemWriter;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(264);
		executor.setMaxPoolSize(264);
		executor.setQueueCapacity(264);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setThreadNamePrefix("MultiThreaded-");
		return executor;
	}

	@Bean WriteStepListner writeStepListner() {
		return new WriteStepListner();
	}
	
	@Bean 
	ReadStepListner readStepListner() {
		return new ReadStepListner();
	}
}
