package com.java.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableBatchProcessing
//@ComponentScan(basePackages = { "com.java.batch.*" })
//@EntityScan("come.java.batch.model.*")
//@EnableJpaRepositories(basePackages = {"come.java.batch.repo"}) 
@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })
@EnableSwagger2
@EnableKafka
public class SpringBatchDbGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchDbGraphApplication.class, args);
	}

}
